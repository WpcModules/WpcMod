package net.wapic.wpcmod.util.render.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Camera
import net.wapic.wpcmod.util.render.WpcModRenderPipelines
import net.wapic.wpcmod.util.render.addVertex
import net.wapic.wpcmod.util.render.state.FilledBoxRenderState

object FilledBoxRenderer : Renderer<FilledBoxRenderState> {

	override val pipeline get() = WpcModRenderPipelines.QUADS

	override fun submit(state: FilledBoxRenderState, pose: PoseStack.Pose, camera: Camera, consumer: VertexConsumer) {

		// top and bottom
		for (i in 0..1) {
			val y = if (i == 0) state.y else state.y2
			consumer.addVertex(pose, state.x, y, state.z, state.color)
			consumer.addVertex(pose, state.x2, y, state.z, state.color)
			consumer.addVertex(pose, state.x2, y, state.z2, state.color)
			consumer.addVertex(pose, state.x, y, state.z2, state.color)
		}

		// left and right
		for (i in 0..1) {
			val z = if (i == 0) state.z else state.z2
			consumer.addVertex(pose, state.x, state.y, z, state.color)
			consumer.addVertex(pose, state.x, state.y2, z, state.color)
			consumer.addVertex(pose, state.x2, state.y2, z, state.color)
			consumer.addVertex(pose, state.x2, state.y, z, state.color)
		}

		// front and back
		for (i in 0..1) {
			val x = if (i == 0) state.x else state.x2
			consumer.addVertex(pose, x, state.y, state.z, state.color)
			consumer.addVertex(pose, x, state.y, state.z2, state.color)
			consumer.addVertex(pose, x, state.y2, state.z2, state.color)
			consumer.addVertex(pose, x, state.y2, state.z, state.color)
		}
	}
}