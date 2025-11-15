package net.wapic.wpcmod.hud

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.util.Colors
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.render.fillWithOutline
import org.lwjgl.glfw.GLFW
import java.awt.Point
import kotlin.math.abs

class HudEditor : Screen {
	private val elements: List<SimpleHudElement>
	private var isScaling: Boolean = false
	private var clickedElement: SimpleHudElement? = null
	private var offsetX: Double = 0.0
	private var offsetY: Double = 0.0
	private var oppositeCorner: Point = Point()
	private var scalePerDistance: Double = 0.0
	val borderColour = ChromaColour.fromStaticRGB(125, 125, 125, 255)
	val backgroundColour = ChromaColour.fromStaticRGB(0, 0, 0, 125)

	constructor(elements: List<SimpleHudElement>) : super(Text.of("Hud Editor")) {
		this.elements = elements.filter { it.isEnabled }
	}

	override fun render(context: DrawContext, mouseX: Int, mouseY: Int, deltaTicks: Float) {
		super.render(context, mouseX, mouseY, deltaTicks)

		elements.forEach {
			context.matrices.push()
			it.applyTransformations(context.matrices)
			context.fillWithOutline(0, 0, it.getUnscaledWidth(), it.getUnscaledHeight(), backgroundColour, borderColour)
			context.drawCenteredTextWithShadow(
				this.textRenderer,
				it.label,
				it.getUnscaledWidth() / 2,
				it.getUnscaledHeight() / 2 - this.textRenderer.fontHeight / 2,
				Colors.WHITE
			)
			context.matrices.pop()
		}
	}

	override fun renderBackground(context: DrawContext, mouseX: Int, mouseY: Int, deltaTicks: Float) { }

	fun getHoveredElement(mouseX: Double, mouseY: Double): SimpleHudElement? {
		return elements.find {
			mouseX in it.getAbsoluteX()..it.getAbsoluteX() + it.getEffectiveWidth() &&
			mouseY in it.getAbsoluteY()..it.getAbsoluteY() + it.getEffectiveHeight()
		}
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		clickedElement = getHoveredElement(mouseX, mouseY)?.also {
			offsetX = mouseX - it.getAbsoluteX()
			offsetY = mouseY - it.getAbsoluteY()

			if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				isDragging = true
				return@also
			}

			if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && it.canScale) {
				oppositeCorner = getOppositeCorner(
					mouseX,
					mouseY,
					it.getAbsoluteX(),
					it.getAbsoluteY(),
					it.getEffectiveWidth(),
					it.getEffectiveHeight()
				)
				scalePerDistance = it.scale / oppositeCorner.distance(mouseX, mouseY)
				isScaling = true
				return@also
			}
		}

		return super.mouseClicked(mouseX, mouseY, button)
	}

	fun getOppositeCorner(mouseX: Double, mouseY: Double, x: Float, y: Float, width: Int, height: Int): Point {
		return Point(
			(if(abs(mouseX - x) > abs(x + width - mouseX)) x else x + width).toInt(),
			(if(abs(mouseY - y) > abs(y + height - mouseY)) y else y + height).toInt()
		)
	}

	override fun mouseMoved(mouseX: Double, mouseY: Double) {
		clickedElement?.let {
			if (isDragging) {
				val x = (mouseX - offsetX).coerceIn(0.0, (MC.window.scaledWidth - it.getEffectiveWidth()).toDouble())
				val y = (mouseY - offsetY).coerceIn(0.0, (MC.window.scaledHeight - it.getEffectiveHeight()).toDouble())

				it.x = (x / (MC.window.scaledWidth - it.getEffectiveWidth())).toFloat()
				it.y = (y / (MC.window.scaledHeight - it.getEffectiveHeight())).toFloat()
				return
			}

			if (isScaling) {
				val newScale = oppositeCorner.distance(mouseX, mouseY) * scalePerDistance
				if(newScale !in 0.2f..5f)  return

				it.scale = newScale.toFloat()
				val translatedPos = translate(oppositeCorner, it)
				it.x = translatedPos.first.coerceIn(0f, (MC.window.scaledWidth - it.getEffectiveWidth()).toFloat()) / (MC.window.scaledWidth - it.getEffectiveWidth()).toFloat()
				it.y = translatedPos.second.coerceIn(0f, (MC.window.scaledWidth - it.getEffectiveWidth()).toFloat()) / (MC.window.scaledHeight - it.getEffectiveHeight()).toFloat()
			}
		}

		super.mouseMoved(mouseX, mouseY)
	}

	fun translate(pos: Point, element: SimpleHudElement): Pair<Float, Float> {
		return (pos.x + element.getEffectiveWidth() * if(pos.x > element.getAbsoluteX() + element.getEffectiveWidth() / 2) -1f else 0f) to
				(pos.y + element.getEffectiveHeight() * if(pos.y > element.getAbsoluteY() + element.getEffectiveHeight() / 2) -1f else 0f)
	}

	override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
		clickedElement = null
		isScaling = false
		isDragging = false
		return super.mouseReleased(mouseX, mouseY, button)
	}

	override fun close() {
		HudManager.saveLocations()
		super.close()
	}
}