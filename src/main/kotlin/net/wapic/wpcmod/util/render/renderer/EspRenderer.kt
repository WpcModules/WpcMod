package net.wapic.wpcmod.util.render.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Camera
import net.wapic.wpcmod.util.render.WpcModRenderPipelines
import net.wapic.wpcmod.util.render.line
import net.wapic.wpcmod.util.render.state.EspRenderState

object EspRenderer : Renderer<EspRenderState> {

	override val pipeline get() = WpcModRenderPipelines.LINES

	override fun submit(state: EspRenderState, pose: PoseStack.Pose, camera: Camera, consumer: VertexConsumer) {
		val height = state.height
		val width = state.width
		val config = state.config

		if (config.box) {
			for (yVal in listOf(state.pos.y, state.pos.y + height)) {
				consumer.line(pose, state.pos.x, yVal, state.pos.z, state.pos.x + width, yVal, state.pos.z, config.color, 2f)
				consumer.line(pose, state.pos.x + width, yVal, state.pos.z, state.pos.x + width, yVal, state.pos.z + width, config.color, 2f)
				consumer.line(pose, state.pos.x + width, yVal, state.pos.z + width, state.pos.x, yVal, state.pos.z + width, config.color, 2f)
				consumer.line(pose, state.pos.x, yVal, state.pos.z + width, state.pos.x, yVal, state.pos.z, config.color, 2f)
			}

			for (xVal in listOf(state.pos.x, state.pos.x + width)) {
				for (zVal in listOf(state.pos.z, state.pos.z + width)) {
					consumer.line(pose, xVal, state.pos.y, zVal, xVal, state.pos.y + height, zVal, config.color, 2f)
				}
			}
		}

		if (config.tracer) {
			val camera = camera.position().toVector3f().add(camera.forwardVector())
			consumer.line(
				pose,
				state.pos.x + width / 2,
				state.pos.y + height / 2,
				state.pos.z + width / 2,
				camera.x,
				camera.y,
				camera.z,
				config.color,
				config.tracerWidth
			)
		}
	}
}