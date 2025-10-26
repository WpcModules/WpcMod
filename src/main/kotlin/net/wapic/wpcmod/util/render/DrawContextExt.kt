package net.wapic.wpcmod.util.render

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.RenderLayer
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.wapic.wpcmod.util.MC

fun DrawContext.drawTexture(
	sprite: Identifier,
	x: Int,
	y: Int,
	u: Float,
	v: Float,
	width: Int,
	height: Int,
	textureWidth: Int,
	textureHeight: Int
) =
	this.drawTexture(RenderLayelabelr::getGuiTextured, sprite, x, y, u, v, width, height, textureWidth, textureHeight)

fun DrawContext.drawGuiTexture(sprite: Identifier, x: Int, y: Int, width: Int, height: Int) =
	this.drawGuiTexture(RenderLayer::getGuiTextured, sprite, x, y, width, height)

fun DrawContext.drawText(text: String, x: Int, y: Int, color: Int, shadow: Boolean) =
	this.drawText(MC.textRenderer, text, x, y, color, shadow)

fun DrawContext.drawTextWithOutline(text: String, x: Float, y: Float, color: Int, outlineColor: Int) {
	draw { consumerProvider ->
		MC.textRenderer.drawWithOutline(
			Text.of { text }.asOrderedText(),
			x, y,
			color, outlineColor,
			matrices.peek().positionMatrix,
			consumerProvider,
			LightmapTextureManager.MAX_LIGHT_COORDINATE
		)
	}
}
