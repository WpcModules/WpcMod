package net.wapic.wpcmod.hud

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
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

	constructor(elements: List<SimpleHudElement>) : super(Component.nullToEmpty("Hud Editor")) {
		this.elements = elements.filter { it.isEnabled }
	}

	override fun extractRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, deltaTicks: Float) {
		super.extractRenderState(context, mouseX, mouseY, deltaTicks)

		elements.forEach {
			context.pose().pushMatrix()
			it.applyTransformations(context.pose())
			context.fillWithOutline(0, 0, it.getUnscaledWidth(), it.getUnscaledHeight(), backgroundColour, borderColour)
			context.centeredText(
				this.font,
				it.label,
				it.getUnscaledWidth() / 2,
				it.getUnscaledHeight() / 2 - this.font.lineHeight / 2,
				CommonColors.WHITE
			)
			context.pose().popMatrix()
		}
	}

	override fun extractBackground(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, deltaTicks: Float) {}

	fun getHoveredElement(mouseX: Double, mouseY: Double): SimpleHudElement? {
		return elements.find {
			mouseX in it.getAbsoluteX()..it.getAbsoluteX() + it.getEffectiveWidth() &&
			mouseY in it.getAbsoluteY()..it.getAbsoluteY() + it.getEffectiveHeight()
		}
	}

	override fun mouseClicked(click: MouseButtonEvent, doubled: Boolean): Boolean {
		clickedElement = getHoveredElement(click.x, click.y)?.also {
			offsetX = click.x - it.getAbsoluteX()
			offsetY = click.y - it.getAbsoluteY()

			if (click.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				isDragging = true
				return@also
			}

			if (click.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT && it.canScale) {
				oppositeCorner = getOppositeCorner(click.x, click.y, it.getAbsoluteX(), it.getAbsoluteY(), it.getEffectiveWidth(), it.getEffectiveHeight())
				scalePerDistance = it.scale / oppositeCorner.distance(click.x, click.y)
				isScaling = true
				return@also
			}
		}

		return super.mouseClicked(click, doubled)
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
				val x = (mouseX - offsetX).coerceIn(0.0, (MC.window.guiScaledWidth - it.getEffectiveWidth()).toDouble())
				val y = (mouseY - offsetY).coerceIn(0.0, (MC.window.guiScaledHeight - it.getEffectiveHeight()).toDouble())

				it.x = (x / (MC.window.guiScaledWidth - it.getEffectiveWidth())).toFloat()
				it.y = (y / (MC.window.guiScaledHeight - it.getEffectiveHeight())).toFloat()
				return
			}

			if (isScaling) {
				val newScale = oppositeCorner.distance(mouseX, mouseY) * scalePerDistance
				if(newScale !in 0.2f..5f)  return

				it.scale = newScale.toFloat()
				val translatedPos = translate(oppositeCorner, it)
				it.x = translatedPos.first.coerceIn(0f, (MC.window.guiScaledWidth - it.getEffectiveWidth()).toFloat()) / (MC.window.guiScaledWidth - it.getEffectiveWidth()).toFloat()
				it.y = translatedPos.second.coerceIn(0f, (MC.window.guiScaledWidth - it.getEffectiveWidth()).toFloat()) / (MC.window.guiScaledHeight - it.getEffectiveHeight()).toFloat()
			}
		}

		super.mouseMoved(mouseX, mouseY)
	}

	fun translate(pos: Point, element: SimpleHudElement): Pair<Float, Float> {
		return (pos.x + element.getEffectiveWidth() * if(pos.x > element.getAbsoluteX() + element.getEffectiveWidth() / 2) -1f else 0f) to
				(pos.y + element.getEffectiveHeight() * if(pos.y > element.getAbsoluteY() + element.getEffectiveHeight() / 2) -1f else 0f)
	}

	override fun mouseReleased(click: MouseButtonEvent): Boolean {
		clickedElement = null
		isScaling = false
		isDragging = false
		return super.mouseReleased(click)
	}

	override fun onClose() {
		HudManager.saveLocations()
		super.onClose()
	}
}