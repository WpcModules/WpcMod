package net.wapic.wpcmod.util.render.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.wapic.wpcmod.util.render.WpcModRenderPipelines
import net.wapic.wpcmod.util.render.line
import net.wapic.wpcmod.util.render.state.LineRenderState

object LineRenderer : Renderer<LineRenderState> {

	override val pipeline get() = WpcModRenderPipelines.LINES

	override fun submit(state: LineRenderState, poseStack: PoseStack, consumer: VertexConsumer) {
		val firstPos = state.firstPos
		val secondPos = state.secondPos
		val color = state.color
		val lineWidth = state.lineWidth
		val pose = poseStack.last()
		consumer.line(
			pose,
			firstPos.x(), firstPos.y(), firstPos.z(),
			secondPos.x(), secondPos.y(), secondPos.z(),
			color,
			lineWidth
		)
	}
}