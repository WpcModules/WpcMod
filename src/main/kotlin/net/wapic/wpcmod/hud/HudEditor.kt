package net.wapic.wpcmod.hud

import net.minecraft.client.gui.Click
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import java.awt.Point

class HudEditor : Screen {
	val elements: List<SimpleHudElement>

	constructor(elements: List<SimpleHudElement>) : super(Text.of("Hud Editor")) {
		this.elements = elements
	}

	override fun render(context: DrawContext, mouseX: Int, mouseY: Int, deltaTicks: Float) {
		super.render(context, mouseX, mouseY, deltaTicks)

		elements.forEach {
			context.matrices.pushMatrix()
			it.applyTransformations(context.matrices)
			context.fill(0, 0, it.getUnscaledWidth(), it.getUnscaledHeight(), 0xffffff)
			context.drawCenteredTextWithShadow(
				this.textRenderer,
				it.label,
				it.getUnscaledWidth() / 2,
				it.getUnscaledHeight() / 2,
				0xffffff
			)
			context.matrices.popMatrix()
		}
	}

	override fun mouseDragged(click: Click, offsetX: Double, offsetY: Double): Boolean {
		val mousePos = Point(offsetX.toInt(), offsetY.toInt())
		val element = elements.find {
			click.x.toInt() in it.position.x..it.getEffectiveWidth() &&
			click.y.toInt() in it.position.y..it.getEffectiveHeight()
		}

		if (click.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			element?.position = mousePos
			return false
		}

		if (click.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			val distance = mousePos.distanceSq(click.x, click.y)

			val newScale = distance * 0.2
			if(newScale < 0.2)
				return false

			element?.scale = newScale.toFloat()
			return true
		}
		return false
	}

	override fun close() {
		HudManager.saveLocations()
		super.close()
	}
}