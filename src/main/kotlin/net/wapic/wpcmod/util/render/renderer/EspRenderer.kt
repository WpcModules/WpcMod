package net.wapic.wpcmod.util.render.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.wapic.wpcmod.util.render.WpcModRenderPipelines
import net.wapic.wpcmod.util.render.line
import net.wapic.wpcmod.util.render.state.EspRenderState

object EspRenderer : Renderer<EspRenderState> {

	override val pipeline get() = WpcModRenderPipelines.LINES

	override fun submit(state: EspRenderState, poseStack: PoseStack, consumer: VertexConsumer) {
		val pose = poseStack.last()
		val pos = state.pos
		val cameraPos = state.camera
		val height = state.height
		val width = state.width
		val config = state.config

		if (config.box) {
			for (yVal in listOf(pos.y, pos.y + height)) {
				consumer.line(pose, pos.x, yVal, pos.z, pos.x + width, yVal, pos.z, config.color, 2f)
				consumer.line(pose, pos.x + width, yVal, pos.z, pos.x + width, yVal, pos.z + width, config.color, 2f)
				consumer.line(pose, pos.x + width, yVal, pos.z + width, pos.x, yVal, pos.z + width, config.color, 2f)
				consumer.line(pose, pos.x, yVal, pos.z + width, pos.x, yVal, pos.z, config.color, 2f)
			}

			for (xVal in listOf(pos.x, pos.x + width)) {
				for (zVal in listOf(pos.z, pos.z + width)) {
					consumer.line(pose, xVal, pos.y, zVal, xVal, pos.y + height, zVal, config.color, 2f)
				}
			}
		}

		if (config.tracer) {
			consumer.line(
				pose,
				pos.x + width / 2,
				pos.y + height / 2,
				pos.z + width / 2,
				cameraPos.x,
				cameraPos.y,
				cameraPos.z,
				config.color,
				config.tracerWidth
			)
		}
	}
}