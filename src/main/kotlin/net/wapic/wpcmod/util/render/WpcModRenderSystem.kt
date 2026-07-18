package net.wapic.wpcmod.util.render

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelTerrainRenderContext
import net.minecraft.util.profiling.Profiler
import net.wapic.wpcmod.events.WorldRenderEvent

object WpcModRenderSystem {

	fun init() {
		LevelRenderEvents.START_MAIN.register(::startMainRendering)
		LevelRenderEvents.END_MAIN.register(::endMainRendering)
	}

	fun close() {
		WpcModRenderer.close()
	}

	private fun startMainRendering(levelRenderContext: LevelTerrainRenderContext) {
		WpcModRenderer.prepare()
	}

	private fun endMainRendering(levelRenderContext: LevelRenderContext) {
		val profiler = Profiler.get()
		profiler.push("WpcModRenderer")
		WorldRenderEvent.EVENT.invoker().onRenderWorld(
			WorldRenderContext(levelRenderContext.poseStack(), levelRenderContext.levelState().cameraRenderState, profiler)
		)

		WpcModRenderer.executeDraws()
		profiler.pop()
	}
}