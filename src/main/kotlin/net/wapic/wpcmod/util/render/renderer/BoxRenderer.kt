package net.wapic.wpcmod.util.render.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Camera
import net.wapic.wpcmod.util.render.WpcModRenderPipelines
import net.wapic.wpcmod.util.render.line
import net.wapic.wpcmod.util.render.state.BoxRenderState

object BoxRenderer : Renderer<BoxRenderState> {

	override val pipeline get() = WpcModRenderPipelines.LINES

	override fun submit(state: BoxRenderState, pose: PoseStack.Pose, camera: Camera, consumer: VertexConsumer) {
		for (yVal in listOf(state.y, state.y2)) {
			consumer.line(pose, state.x, yVal, state.z, state.x2, yVal, state.z, state.color, state.lineWidth)
			consumer.line(pose, state.x2, yVal, state.z, state.x2, yVal, state.z2, state.color, state.lineWidth)
			consumer.line(pose, state.x2, yVal, state.z2, state.x, yVal, state.z2, state.color, state.lineWidth)
			consumer.line(pose, state.x, yVal, state.z2, state.x, yVal, state.z, state.color, state.lineWidth)
		}

		for (xVal in listOf(state.x, state.x2)) {
			for (zVal in listOf(state.z, state.z2)) {
				consumer.line(pose, xVal, state.y, zVal, xVal, state.y2, zVal, state.color, state.lineWidth)
			}
		}
	}
}