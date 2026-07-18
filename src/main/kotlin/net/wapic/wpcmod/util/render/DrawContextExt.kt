package net.wapic.wpcmod.util.render

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier
import net.wapic.wpcmod.util.render.state.RoundedRectangleRenderState
import org.joml.Matrix3x2f

fun GuiGraphicsExtractor.drawTexture(
	sprite: Identifier,
	x: Int,
	y: Int,
	u: Float,
	v: Float,
	width: Int,
	height: Int,
	textureWidth: Int,
	textureHeight: Int
) = this.blit(RenderPipelines.GUI_TEXTURED, sprite, x, y, u, v, width, height, textureWidth, textureHeight)

fun GuiGraphicsExtractor.fillWithOutline(
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	color: ChromaColour,
	outlineColor: ChromaColour
) {
	val color = color.getEffectiveColourRGB()
	fill(x, y, x + width, y + height, color)
	drawBorder(x, y, width, height, outlineColor)
}

fun GuiGraphicsExtractor.drawBorder(x: Int, y: Int, width: Int, height: Int, color: ChromaColour) {
	val color = color.getEffectiveColourRGB()
	fill(x, y, x + width, y + 1, color)
	fill(x, y + height - 1, x + width, y + height, color)
	fill(x, y + 1, x + 1, y + height - 1, color)
	fill(x + width - 1, y + 1, x + width, y + height - 1, color)
}

fun GuiGraphicsExtractor.drawRoundedRect(x: Int, y: Int, width: Int, height: Int, radius: Float, color: ChromaColour) {
	val matrix = Matrix3x2f(pose())
	this.guiRenderState.addGuiElement(
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