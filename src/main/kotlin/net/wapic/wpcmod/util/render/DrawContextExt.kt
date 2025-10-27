package net.wapic.wpcmod.util.render

import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
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
) = this.drawTexture(RenderPipelines.GUI_TEXTURED, sprite, x, y, u, v, width, height, textureWidth, textureHeight)

fun DrawContext.drawGuiTexture(sprite: Identifier, x: Int, y: Int, width: Int, height: Int) =
	this.drawGuiTexture(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height)

fun DrawContext.drawText(text: String, x: Int, y: Int, color: Int, shadow: Boolean) =
	this.drawText(MC.textRenderer, text, x, y, color, shadow)
