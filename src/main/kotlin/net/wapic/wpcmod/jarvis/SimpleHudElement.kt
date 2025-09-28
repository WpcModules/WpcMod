package net.wapic.wpcmod.jarvis

import moe.nea.jarvis.api.JarvisScalable
import net.minecraft.text.Text

abstract class SimpleHudElement(
	var xPos: Double = 0.0,
	var yPos: Double = 0.0,
	var text: Text = Text.literal(""),
	var w: Int = 0,
	var h: Int = 0,
	var defaultScale: Float = 1f
) : JarvisScalable {

	override fun getX(): Double {
		return xPos
	}

	override fun setX(newX: Double) {
		xPos = newX
	}

	override fun getY(): Double {
		return yPos
	}

	override fun setY(newY: Double) {
		yPos = newY
	}

	override fun getLabel(): Text {
		return text
	}

	override fun getWidth(): Int {
		return w
	}

	override fun getHeight(): Int {
		return h
	}

	override fun getScale(): Float {
		return defaultScale
	}

	override fun setScale(newScale: Float) {
		defaultScale = newScale
	}
}