package net.wapic.wpcmod.hud

import net.minecraft.client.gui.Click
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.wapic.wpcmod.util.MC
import org.lwjgl.glfw.GLFW
import kotlin.math.pow
import kotlin.math.sqrt

class HudEditor : Screen(Text.of { "Hud Editor" }) {

	var clickedElement: SimpleHudElement? = null
	val clickPos: Pair<Double, Double>? = null

	override fun render(context: DrawContext, mouseX: Int, mouseY: Int, deltaTicks: Float) {
		HudManager.hudElements.forEach { hudElement ->
			context.fill(0, 0, hudElement.getUnscaledWidth(), hudElement.getUnscaledHeight(), 0x800000)
			context.drawCenteredTextWithShadow(
				MC.textRenderer,
				hudElement.label,
				hudElement.position.first + hudElement.getUnscaledWidth() / 2,
				hudElement.position.second + hudElement.getUnscaledHeight() / 2,
				0xffffff
			)
		}
	}

	override fun mouseClicked(click: Click, doubled: Boolean): Boolean {
		HudManager.hudElements.forEach { hud ->
			val x = hud.position.first.toDouble()
			val y = hud.position.second.toDouble()
			val width = hud.getEffectiveWidth().toDouble()
			val height = hud.getEffectiveHeight().toDouble()

			if (click.x in x..width && click.y in y..height) {
				clickedElement = hud
				return true
			}
		}
		return false
	}

	override fun mouseReleased(click: Click): Boolean {
		clickedElement = null
		return true
	}

	override fun mouseDragged(click: Click, offsetX: Double, offsetY: Double): Boolean {
		clickedElement?.let {
			if (click.button() == GLFW.GLFW_MOUSE_BUTTON_1) {
				it.position = click.x.toInt() to click.y.toInt()
				return true
			} else if (click.button() == GLFW.GLFW_MOUSE_BUTTON_2) {
				clickPos?.let { click ->
					it.scale = getDistance(
						click.first,
						it.position.first.toDouble(),
						click.second,
						it.position.second.toDouble()
					).toFloat()
					return true
				}
			}
		}
		return false
	}

	override fun close() {
		HudManager.saveLocations()
		super.close()
	}

	fun getDistance(x: Double, y: Double, x2: Double, y2: Double): Double {
		return sqrt((x - x2).pow(2) + (y - y2).pow(2))
	}

}