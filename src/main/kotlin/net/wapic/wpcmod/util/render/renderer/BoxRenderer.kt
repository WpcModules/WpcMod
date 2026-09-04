package net.wapic.wpcmod.util.render.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.wapic.wpcmod.util.render.WpcModRenderPipelines
import net.wapic.wpcmod.util.render.line
import net.wapic.wpcmod.util.render.state.BoxRenderState

object BoxRenderer : Renderer<BoxRenderState> {

	override val pipeline get() = WpcModRenderPipelines.LINES

	override fun submit(state: BoxRenderState, poseStack: PoseStack, consumer: VertexConsumer) {
		val pose = poseStack.last()
		val x = state.x
		val y = state.y
		val z = state.z
		val x2 = state.x2
		val y2 = state.y2
		val z2 = state.z2
		val color = state.color
		val lineWidth = state.lineWidth

		for (yVal in listOf(y, y2)) {
			consumer.line(pose, x, yVal, z, x2, yVal, z, color, lineWidth)
			consumer.line(pose, x2, yVal, z, x2, yVal, z2, color, lineWidth)
			consumer.line(pose, x2, yVal, z2, x, yVal, z2, color, lineWidth)
			consumer.line(pose, x, yVal, z2, x, yVal, z, color, lineWidth)
		}

		for (xVal in listOf(x, x2)) {
			for (zVal in listOf(z, z2)) {
				consumer.line(pose, xVal, y, zVal, xVal, y2, zVal, color, lineWidth)
			}
		}
	}
}