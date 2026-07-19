package net.wapic.wpcmod.util.render

import com.mojang.blaze3d.buffers.GpuBufferSlice
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.systems.RenderPass
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.renderer.StagedVertexBuffer
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.util.profiling.Profiler
import net.wapic.wpcmod.util.MC
import org.joml.Vector4f
import java.util.Optional
import java.util.OptionalDouble

// Modified from Skyblocker
object WpcModRenderer {

	private val vertexBuffer: StagedVertexBuffer = StagedVertexBuffer({ "WpcMod Renderer Vertex Buffer" }, RenderType.SMALL_BUFFER_SIZE)

	private var previousPipeline: RenderPipeline? = null
	private var previousDraw: StagedVertexBuffer.Draw? = null
	private var previousTextureSetup: TextureSetup? = null
	private var previousAlphaModifier: Float = 1f

	private val DRAWS = mutableListOf<Draw>()

	fun getBuffer(pipeline: RenderPipeline): VertexConsumer {
		return getBuffer(pipeline, TextureSetup.noTexture(), 1f)
	}

	fun getBuffer(pipeline: RenderPipeline, textureSetup: TextureSetup): VertexConsumer {
		return getBuffer(pipeline, textureSetup, 1f)
	}

	fun getBuffer(pipeline: RenderPipeline, textureSetup: TextureSetup, alphaModifier: Float): VertexConsumer {
		if(previousDraw == null || previousPipeline != pipeline || previousTextureSetup != textureSetup || previousAlphaModifier != alphaModifier) {
			previousDraw = vertexBuffer.appendDraw(pipeline.getVertexFormatBinding(0)!!, pipeline.primitiveTopology)
			DRAWS.add(Draw(previousDraw!!, pipeline, textureSetup, alphaModifier))
		}
		return vertexBuffer.getVertexBuilder(previousDraw!!)
	}

	fun prepare() {
		previousDraw = null
		previousPipeline = null
		previousTextureSetup = null
		previousAlphaModifier = 1f
	}

	fun executeDraws() {
		val profiler = Profiler.get()
		profiler.push("upload")
		vertexBuffer.upload()

		profiler.popPush("draw")
		dispatchDraws()

		profiler.popPush("endFrame")
		vertexBuffer.endFrame()
		DRAWS.clear()

		profiler.pop()
	}

	private fun dispatchDraws() {
		val mainRenderTarget = MC.instance.gameRenderer.mainRenderTarget()

		RenderSystem.getDevice().createCommandEncoder().createRenderPass(
			{"Custom Level Renderer"},
			mainRenderTarget.colorTextureView!!,
			Optional.empty(),
			mainRenderTarget.depthTextureView,
			OptionalDouble.empty()
		).use { renderPass ->

			RenderSystem.bindDefaultUniforms(renderPass)

			for(draw in DRAWS) {
				draw(draw, renderPass)
			}
		}
	}

	private fun draw(draw: Draw, renderPass: RenderPass) {
		val executeInfo: StagedVertexBuffer.ExecuteInfo = vertexBuffer.getExecuteInfo(draw.draw) ?: return

		renderPass.setPipeline(draw.pipeline)
		renderPass.setUniform("DynamicTransforms", setupDynamicTransforms(draw.alphaModifier))

		draw.textureSetup.texure0?.let {
			renderPass.bindTexture("Sampler0", it, draw.textureSetup.sampler0)
		}

		draw.textureSetup.texure1?.let {
			renderPass.bindTexture("Sampler1", it, draw.textureSetup.sampler1)
		}

		draw.textureSetup.texure2?.let {
			renderPass.bindTexture("Sampler2", it, draw.textureSetup.sampler2)
		}

		renderPass.setVertexBuffer(0, executeInfo.vertexBuffer.slice())
		renderPass.setIndexBuffer(executeInfo.indexBuffer, executeInfo.indexType)

		renderPass.drawIndexed(executeInfo.indexCount, 1, executeInfo.firstIndex, executeInfo.baseVertex, 0)
	}

	private fun setupDynamicTransforms(alphaModifier: Float): GpuBufferSlice {
		return RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrixCopy(), Vector4f(1f, 1f, 1f, alphaModifier))
	}

	private fun applyViewOffsetZLayering() {
		val modelViewStack = RenderSystem.getModelViewStack()
		modelViewStack.pushMatrix()
		RenderSystem.getProjectionType().applyLayeringTransform(modelViewStack, 1f)
	}

	private fun unApplyViewOffsetZLayering() {
		RenderSystem.getModelViewStack().popMatrix()
	}

	fun close() {
		vertexBuffer.close()
	}

	data class Draw(val draw: StagedVertexBuffer.Draw, val pipeline: RenderPipeline, val textureSetup: TextureSetup, val alphaModifier: Float)
}