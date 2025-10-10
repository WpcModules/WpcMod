package net.wapic.wpcmod.features.funnymap.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.funnymap.core.map.RoomState
import net.wapic.wpcmod.util.render.RenderUtils2D
import net.wapic.wpcmod.util.render.drawText
import org.lwjgl.opengl.GL11
import java.awt.Color

object RenderUtilsGL {

	val config get() = WpcMod.config.funnyMap

	private val tr = MinecraftClient.getInstance().textRenderer

	fun preDraw() {
		GL11.glEnable(GL11.GL_ALPHA_TEST)
		GL11.glEnable(GL11.GL_BLEND)
		GL11.glDisable(GL11.GL_DEPTH_TEST)
		GL11.glDisable(GL11.GL_LIGHTING)
		GL11.glDisable(GL11.GL_TEXTURE_2D)
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
	}

	fun postDraw() {
		GL11.glDisable(GL11.GL_BLEND)
		GL11.glEnable(GL11.GL_DEPTH_TEST)
		GL11.glEnable(GL11.GL_TEXTURE_2D)
	}

	fun renderRect(drawContext: DrawContext, x: Number, y: Number, w: Number, h: Number, color: Color) {
		if (color.alpha == 0) return
		preDraw()
		color.bind()

		//worldRenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION)
		RenderUtils2D.addQuadVertices(x.toDouble(), y.toDouble(), w.toDouble(), h.toDouble())
		//tessellator.draw()

		postDraw()
	}

	fun renderRectBorder(
		drawContext: DrawContext,
		x: Double,
		y: Double,
		w: Double,
		h: Double,
		thickness: Double,
		color: Color
	) {
		if (color.alpha == 0) return
		preDraw()
		color.bind()

		//worldRenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION)
		RenderUtils2D.addQuadVertices(x - thickness, y, thickness, h)
		RenderUtils2D.addQuadVertices(x - thickness, y - thickness, w + thickness * 2, thickness)
		RenderUtils2D.addQuadVertices(x + w, y, thickness, h)
		RenderUtils2D.addQuadVertices(x - thickness, y + h, w + thickness * 2, thickness)
		//tessellator.draw()

		postDraw()
	}

	fun renderCenteredText(drawContext: DrawContext, text: List<String>, x: Int, y: Int, color: Color) {
		if (text.isEmpty()) return
		val matrixStack = drawContext.matrices

		matrixStack.push()
		matrixStack.translate(x.toFloat(), y.toFloat(), 0f)
		matrixStack.scale(config.textScale, config.textScale, 1f)

		if (config.mapRotate) {
			//matrixStack.peek().rotate(mc.player?.yaw + 180f, 0f, 0f, 1f)
		}
		val fontHeight = tr.fontHeight + 1
		val yTextOffset = text.size * fontHeight / -2f

		text.withIndex().forEach { (index, text) ->
			drawContext.drawText(
				text,
				tr.getWidth(text) / -2,
				(yTextOffset + index * fontHeight).toInt(),
				color.rgb,
				true
			)
		}

		matrixStack.pop()
	}

	fun drawCheckmark(drawContext: DrawContext, x: Float, y: Float, state: RoomState) {
		/*
        val (checkmark, size) = when (config.mapCheckmark) {
            else -> return
        }
        if (checkmark != null) {
            GL11.glColor4f(1f, 1f, 1f, 1f)
            GL11.glEnable(GL11.GL_ALPHA_TEST)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            checkmark.bind()

            RenderUtils2D.drawTexturedQuad(
                x + (MapUtils.roomSize - size) / 2,
                y + (MapUtils.roomSize - size) / 2,
                size,
                size
            )
        }
        */
	}

	private fun Color.bind() {
		GL11.glColor4f(this.red / 255f, this.green / 255f, this.blue / 255f, this.alpha / 255f)
	}
}
