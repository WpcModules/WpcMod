package net.wapic.wpcmod.util.render

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.VertexConsumer
import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.renderer.state.gui.GuiElementRenderState
import net.wapic.wpcmod.render.DirectVertexConsumer
import org.joml.Matrix3x2f

@JvmRecord
data class RoundedRectangleRenderState(
	val matrix: Matrix3x2f,
	val x: Float, val y: Float,
	val width: Float, val height: Float,
	val radius: Float,
	val chromaColour: ChromaColour,
	val screenBounds: ScreenRectangle?,
) : GuiElementRenderState {

	override fun buildVertices(vertices: VertexConsumer) {
		val color = chromaColour.getEffectiveColour()

		val consumer = DirectVertexConsumer(vertices as? BufferBuilder, false)
		consumer.addVertexWith2DPose(matrix, x, y + height).setUv(0f, 0f).setUv(width, height).setUv(radius, radius)
			.setUv(radius, radius).setColor(color.red, color.green, color.blue, color.alpha)
		consumer.addVertexWith2DPose(matrix, x + width, y + height).setUv(width, 0f).setUv(width, height)
			.setUv(radius, radius)
			.setUv(radius, radius).setColor(color.red, color.green, color.blue, color.alpha)
		consumer.addVertexWith2DPose(matrix, x + width, y).setUv(width, height).setUv(width, height)
			.setUv(radius, radius)
			.setUv(radius, radius).setColor(color.red, color.green, color.blue, color.alpha)
		consumer.addVertexWith2DPose(matrix, x, y).setUv(0f, height).setUv(width, height).setUv(radius, radius)
			.setUv(radius, radius).setColor(color.red, color.green, color.blue, color.alpha)
	}

	override fun pipeline(): RenderPipeline {
		return WpcModRenderPipelines.GUI_THING
	}

	override fun textureSetup(): TextureSetup {
		return TextureSetup.noTexture()
	}

	override fun scissorArea(): ScreenRectangle? {
		return null
	}

	override fun bounds(): ScreenRectangle? {
		return screenBounds
	}

	companion object {
		fun createBounds(matrix3x2f: Matrix3x2f, x: Float, y: Float, width: Float, height: Float): ScreenRectangle {
			return (ScreenRectangle(x.toInt(), y.toInt(), width.toInt(), height.toInt())).transformMaxBounds(matrix3x2f)
		}
	}
}