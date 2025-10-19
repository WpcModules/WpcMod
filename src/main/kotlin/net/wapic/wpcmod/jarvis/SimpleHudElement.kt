package net.wapic.wpcmod.jarvis

import moe.nea.jarvis.api.JarvisScalable
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.text.Text
import net.wapic.wpcmod.util.Utils.modIdentifier
import java.util.Locale

abstract class SimpleHudElement(
	var displayLabel: String,
	var w: Int,
	var h: Int,
	var xPos: Double = 0.0,
	var yPos: Double = 0.0,
	var defaultScale: Float = 1f
) : JarvisScalable {

	init {
		val identifier = modIdentifier(displayLabel.lowercase(Locale.US).replace(" ", "_"))

		HudLayerRegistrationCallback.EVENT.register { layeredDrawerWrapper ->
			layeredDrawerWrapper.attachLayerBefore(
				IdentifiedLayer.DEMO_TIMER,
				IdentifiedLayer.of(identifier, ::render)
			)
		}
	}

	abstract fun render(drawContext: DrawContext, renderTickCounter: RenderTickCounter)

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
		return Text.of(displayLabel)
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