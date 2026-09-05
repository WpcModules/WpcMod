package net.wapic.wpcmod.util.render.renderer

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.textures.FilterMode
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.gui.Font.GlyphVisitor
import net.minecraft.client.gui.font.TextRenderable
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.util.ARGB
import net.minecraft.util.LightCoordsUtil
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.render.WpcModRenderPipelines
import net.wapic.wpcmod.util.render.WpcModRenderer
import net.wapic.wpcmod.util.render.state.TextRenderState
import org.joml.Matrix4f
import org.joml.minus

object TextRenderer : Renderer<TextRenderState> {

	override val pipeline: RenderPipeline = WpcModRenderPipelines.TEXT

	override fun submit(
		state: TextRenderState,
		poseStack: PoseStack,
		consumer: VertexConsumer
	) {
		poseStack.pushPose()
		val scale = state.scale * 0.025f
		val pose: Matrix4f = Matrix4f()
			.translate(state.pos.add(.5f, .62f, .5f) - state.cameraPos)
			.rotate(state.cameraOrientation)
			.scale(scale, -scale, scale)
		val backgroundColor = if (state.background) ARGB.color(0.25f, -16777216) else 0
		val color = state.color.getEffectiveColourRGB()

		val preparedText = MC.font.prepareText(state.text, -MC.font.width(state.text) / 2f, 0f, color, state.shadow, backgroundColor)

		preparedText.visit(object : GlyphVisitor {
			override fun acceptRenderable(renderable: TextRenderable) {
				val consumer: VertexConsumer = WpcModRenderer.getConsumer(pipeline, getTextureSetup(renderable))
				renderable.render(pose, consumer, LightCoordsUtil.FULL_BRIGHT, false)
			}
		})

		poseStack.popPose()
	}

	fun getTextureSetup(renderable: TextRenderable): TextureSetup {
		return TextureSetup.singleTextureWithLightmap(renderable.textureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST))
	}
}