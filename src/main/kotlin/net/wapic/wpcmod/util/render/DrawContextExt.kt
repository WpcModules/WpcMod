package net.wapic.wpcmod.util.render

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier

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
	this.drawTexture(RenderLayer::getGuiTextured, sprite, x, y, u, v, width, height, textureWidth, textureHeight)

fun DrawContext.drawGuiTexture(sprite: Identifier, x: Int, y: Int, width: Int, height: Int) =
	this.drawGuiTexture(RenderLayer::getGuiTextured, sprite, x, y, width, height)

fun DrawContext.drawText(text: String, x: Int, y: Int, color: Int, shadow: Boolean) =
	this.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, color, shadow)
