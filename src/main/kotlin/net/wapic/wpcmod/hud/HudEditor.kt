package net.wapic.wpcmod.hud

import net.minecraft.client.gui.Click
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.util.Colors
import org.lwjgl.glfw.GLFW
import java.awt.Point
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

class HudEditor : Screen {
	private val elements: List<SimpleHudElement>
	private val backgroundColor: Int = 0x77121212
	private var isScaling: Boolean = false
	private var clickedElement: SimpleHudElement? = null
	private var offsetX: Double = 0.0
	private var offsetY: Double = 0.0
	private var oppositeAnchor: Point = Point()
	private var scalePerDistance: Double = 0.0

	constructor(elements: List<SimpleHudElement>) : super(Text.of("Hud Editor")) {
		this.elements = elements
	}

	override fun render(context: DrawContext, mouseX: Int, mouseY: Int, deltaTicks: Float) {
		super.render(context, mouseX, mouseY, deltaTicks)

		elements.filter { it.isEnabled }.forEach {
			context.matrices.pushMatrix()
			it.applyTransformations(context.matrices)
			context.fill(0, 0, it.getUnscaledWidth(), it.getUnscaledHeight(), backgroundColor)
			context.drawCenteredTextWithShadow(
				this.textRenderer,
				it.label,
				it.getUnscaledWidth() / 2,
				it.getUnscaledHeight() / 2 - this.textRenderer.fontHeight / 2,
				Colors.WHITE
			)
			context.matrices.popMatrix()
		}
	}

	override fun renderBackground(context: DrawContext, mouseX: Int, mouseY: Int, deltaTicks: Float) { }

	fun getHoveredElement(mouseX: Double, mouseY: Double): SimpleHudElement? {
		return elements.filter { it.isEnabled }.find {
			(mouseX > it.x && mouseX < it.x + it.getEffectiveWidth()) &&
			(mouseY > it.y && mouseY < it.y + it.getEffectiveHeight())
		}
	}

	override fun mouseClicked(click: Click, doubled: Boolean): Boolean {
		clickedElement = getHoveredElement(click.x, click.y)?.also {
			if (click.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				offsetX = click.x - it.x
				offsetY = click.y - it.y
				isDragging = true
				return@also
			}

			if (click.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT && it.canScale) {
				oppositeAnchor = getOppositeAnchor(click.x, click.y, it.x, it.y, it.getEffectiveWidth(), it.getEffectiveHeight())
				scalePerDistance = it.scale / oppositeAnchor.distance(click.x, click.y)
				isScaling = true
				return@also
			}
		}

		return super.mouseClicked(click, doubled)
	}

	fun getOppositeAnchor(mouseX: Double, mouseY: Double, x: Float, y: Float, width: Int, height: Int): Point {
		return Point(
			(if(abs(mouseX - x) > abs(x + width - mouseX)) x else x + width).toInt(),
			(if(abs(mouseY - y) > abs(y + height - mouseY)) y else y + height).toInt()
		)
	}

	override fun mouseMoved(mouseX: Double, mouseY: Double) {
		clickedElement?.let {
			if (isDragging) {
				it.x = (mouseX - offsetX).toFloat()
				it.y = (mouseY - offsetY).toFloat()
				return
			}

			if (isScaling) {
				val newScale = oppositeAnchor.distance(mouseX, mouseY) * scalePerDistance
				if(newScale < 0.2f)  return

				it.scale = newScale.toFloat()
				it.x = (oppositeAnchor.x + it.getEffectiveWidth() * -ceil((floor(it.x).coerceAtLeast(1f) / oppositeAnchor.x) % 1))
				it.y = (oppositeAnchor.y + it.getEffectiveHeight() * -ceil((floor(it.y).coerceAtLeast(1f) / oppositeAnchor.y) % 1))
			}
		}

		super.mouseMoved(mouseX, mouseY)
	}

	override fun mouseReleased(click: Click): Boolean {
		clickedElement = null
		isScaling = false
		isDragging = false
		return super.mouseReleased(click)
	}

	override fun close() {
		HudManager.saveLocations()
		super.close()
	}
}