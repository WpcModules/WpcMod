package net.wapic.wpcmod.util.render.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.wapic.wpcmod.util.render.WpcModRenderPipelines
import net.wapic.wpcmod.util.render.addVertex
import net.wapic.wpcmod.util.render.state.FilledBoxRenderState

object FilledBoxRenderer : Renderer<FilledBoxRenderState> {

	override val pipeline get() = WpcModRenderPipelines.QUADS

	override fun submit(state: FilledBoxRenderState, poseStack: PoseStack, consumer: VertexConsumer) {
		val pose = poseStack.last()
		val x = state.x
		val y = state.y
		val z = state.z
		val x2 = state.x2
		val y2 = state.y2
		val z2 = state.z2
		val color = state.color

		// top and bottom
		for (yVal in listOf(y, y2)) {
			consumer.addVertex(pose, x, yVal, z, color)
			consumer.addVertex(pose, x2, yVal, z, color)
			consumer.addVertex(pose, x2, yVal, z2, color)
			consumer.addVertex(pose, x, yVal, z2, color)
		}

		// left and right
		for (zVal in listOf(z, z2)) {
			consumer.addVertex(pose, x, y, zVal, color)
			consumer.addVertex(pose, x, y2, zVal, color)
			consumer.addVertex(pose, x2, y2, zVal, color)
			consumer.addVertex(pose, x2, y, zVal, color)
		}

		// front and back
		for (xVal in listOf(x, x2)) {
			consumer.addVertex(pose, xVal, y, z, color)
			consumer.addVertex(pose, xVal, y, z2, color)
			consumer.addVertex(pose, xVal, y2, z2, color)
			consumer.addVertex(pose, xVal, y2, z, color)
		}
	}
}