package net.wapic.wpcmod.util.render

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import net.wapic.wpcmod.util.MC
import org.joml.Matrix3x2f

fun GuiGraphics.drawTexture(
	sprite: ResourceLocation,
	x: Int,
	y: Int,
	u: Float,
	v: Float,
	width: Int,
	height: Int,
	textureWidth: Int,
	textureHeight: Int
) = this.blit(RenderPipelines.GUI_TEXTURED, sprite, x, y, u, v, width, height, textureWidth, textureHeight)

fun GuiGraphics.drawText(text: String, x: Int, y: Int, color: Int, shadow: Boolean) =
	this.drawString(MC.textRenderer, text, x, y, color, shadow)

fun GuiGraphics.fillWithOutline(
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	color: ChromaColour,
	outlineColor: ChromaColour
) {
	val color = color.getEffectiveColourRGB()
	val outlineColor = outlineColor.getEffectiveColourRGB()
	fill(x, y, x + width, y + height, color)
	fill(x, y, x + width, y + 1, outlineColor)
	fill(x, y + height - 1, x + width, y + height, outlineColor)
	fill(x, y + 1, x + 1, y + height - 1, outlineColor)
	fill(x + width - 1, y + 1, x + width, y + height - 1, outlineColor)
}

fun GuiGraphics.drawBorder(x: Int, y: Int, width: Int, height: Int, color: ChromaColour) {
	val color = color.getEffectiveColourRGB()
	fill(x, y, x + width, y + 1, color)
	fill(x, y + height - 1, x + width, y + height, color)
	fill(x, y + 1, x + 1, y + height - 1, color)
	fill(x + width - 1, y + 1, x + width, y + height - 1, color)
}

fun GuiGraphics.drawRoundedRect(x: Int, y: Int, width: Int, height: Int, radius: Float, color: ChromaColour) {
	val matrix = Matrix3x2f(pose())
	this.guiRenderState.submitGuiElement(
		RoundedRectangleRenderState(
			matrix,
			x,
			y,
			width,
			height,
			radius,
			color,
			RoundedRectangleRenderState.createBounds(matrix, x, y, width, height)
		)
	)
}