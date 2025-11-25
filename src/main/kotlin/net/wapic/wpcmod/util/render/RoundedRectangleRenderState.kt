package net.wapic.wpcmod.util.render

import com.mojang.blaze3d.pipeline.RenderPipeline
import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.gui.ScreenRect
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState
import net.minecraft.client.render.BufferBuilder
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.texture.TextureSetup
import net.wapic.wpcmod.render.DirectVertexConsumer
import org.joml.Matrix3x2f

@JvmRecord
data class RoundedRectangleRenderState(
	val matrix: Matrix3x2f?,
	val x: Int, val y: Int,
	val width: Int, val height: Int,
	val radius: Float,
	val chromaColour: ChromaColour,
	val screenBounds: ScreenRect?,
) : SimpleGuiElementRenderState {

	override fun setupVertices(vertices: VertexConsumer) {
		val color = chromaColour.getEffectiveColour()

		val x = x.toFloat()
		val y = y.toFloat()
		val width = width.toFloat()
		val height = height.toFloat()

		val consumer = DirectVertexConsumer(vertices as? BufferBuilder, false)
		consumer.vertex(matrix, x, y + height).texture(0f, 0f).texture(width, height).texture(radius, radius)
			.texture(radius, radius).color(color.red, color.green, color.blue, color.alpha)
		consumer.vertex(matrix, x + width, y + height).texture(width, 0f).texture(width, height).texture(radius, radius)
			.texture(radius, radius).color(color.red, color.green, color.blue, color.alpha)
		consumer.vertex(matrix, x + width, y).texture(width, height).texture(width, height).texture(radius, radius)
			.texture(radius, radius).color(color.red, color.green, color.blue, color.alpha)
		consumer.vertex(matrix, x, y).texture(0f, height).texture(width, height).texture(radius, radius)
			.texture(radius, radius).color(color.red, color.green, color.blue, color.alpha)
	}

	override fun pipeline(): RenderPipeline {
		return WpcModRenderPipelines.GUI_THING
	}

	override fun textureSetup(): TextureSetup {
		return TextureSetup.empty()
	}

	override fun scissorArea(): ScreenRect? {
		return null
	}

	override fun bounds(): ScreenRect? {
		return screenBounds
	}

	companion object {
		fun createBounds(matrix3x2f: Matrix3x2f, x: Int, y: Int, width: Int, height: Int): ScreenRect {
			return (ScreenRect(x, y, width, height)).transformEachVertex(matrix3x2f)
		}
	}
}