package net.wapic.wpcmod.util.render.renderer

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.wapic.wpcmod.util.render.state.RenderState

interface Renderer<S : RenderState> {

	val pipeline: RenderPipeline
	fun submit(state: S, poseStack: PoseStack, consumer: VertexConsumer)
}