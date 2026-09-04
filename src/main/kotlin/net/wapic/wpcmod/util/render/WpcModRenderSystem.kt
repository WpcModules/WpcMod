package net.wapic.wpcmod.util.render

import com.mojang.blaze3d.vertex.PoseStack
import net.fabricmc.fabric.api.client.rendering.v1.level.*
import net.minecraft.CrashReport
import net.minecraft.ReportedException
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.util.profiling.Profiler
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.util.render.renderer.*
import net.wapic.wpcmod.util.render.state.*
import net.wapic.wpcmod.util.unaryMinus

@Suppress("UNCHECKED_CAST")
class WpcModRenderSystem {

	private val renderStates = mutableListOf<RenderState>()
	private val renderers = mapOf(
		EspRenderState::class.java to EspRenderer,
		LineRenderState::class.java to LineRenderer,
		BoxRenderState::class.java to BoxRenderer,
		FilledBoxRenderState::class.java to FilledBoxRenderer,
		TextRenderState::class.java to TextRenderer,
	)

	init {
		LevelRenderEvents.START_MAIN.register(::startMainRendering)
		LevelRenderEvents.END_MAIN.register(::endMainRendering)
		LevelExtractionEvents.END_EXTRACTION.register(::onEndExtraction)
	}

	private fun startMainRendering(levelRenderContext: LevelTerrainRenderContext) {
		WpcModRenderer.prepare()
	}

	private fun endMainRendering(levelRenderContext: LevelRenderContext) {
		val profiler = Profiler.get()
		profiler.push("wpcmod")

		profiler.push("submit")
		for (state in renderStates) {
			submit(state, levelRenderContext.poseStack(), levelRenderContext.levelState().cameraRenderState)
		}
		renderStates.clear()
		profiler.pop()

		WpcModRenderer.executeDraws(profiler)

		profiler.pop()
	}

	private fun onEndExtraction(context: LevelExtractionContext) {
		val profiler = Profiler.get()
		profiler.push("wpcmod")

		val partialTicks = context.deltaTracker().getGameTimeDeltaPartialTick(false)
		val extractionContext = WpcModExtractionContext(renderStates, context.level(), context.camera(), partialTicks)

		WorldRenderEvent.EVENT.invoker().onRenderWorld(extractionContext, profiler)

		profiler.pop()
	}

	private fun <S : RenderState> submit(state: S, poseStack: PoseStack, camera: CameraRenderState) {
		try {
			poseStack.pushPose()
			poseStack.translate(-camera.pos)
			val renderer = renderers[state::class.java] as Renderer<S>
			val consumer = WpcModRenderer.getConsumer(renderer.pipeline)
			renderer.submit(state, poseStack, consumer)
			poseStack.popPose()
		} catch (t: Throwable) {
			val report = CrashReport.forThrowable(t, "Rendering ESP in world")
			throw ReportedException(report)
		}
	}

	companion object {

		fun close() {
			WpcModRenderer.close()
		}
	}
}