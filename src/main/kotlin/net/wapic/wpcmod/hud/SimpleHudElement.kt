package net.wapic.wpcmod.hud

import moe.nea.jarvis.api.Jarvis
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.wapic.wpcmod.util.Utils.modIdentifier
import org.joml.Matrix3x2f
import java.util.*

abstract class SimpleHudElement(
	var label: String,
	var width: Int,
	var height: Int,
	var scaleable: Boolean = true,
) {
	var scale = 1f
	var position = 0 to 0

	init {
		val identifier = modIdentifier(label.lowercase(Locale.US).replace(" ", "_"))
		HudElementRegistry.attachElementBefore(VanillaHudElements.DEMO_TIMER, identifier) { context, tickCounter ->
			render(context, tickCounter.dynamicDeltaTicks)
		}
	}

	abstract fun render(drawContext: DrawContext, deltaTicks: Float)

	fun getLabel(): Text {
		return Text.of(label)
	}

	fun getUnscaledWidth(): Int {
		return width
	}

	fun getUnscaledHeight(): Int {
		return height
	}

	fun getEffectiveWidth(): Int {
		if (scaleable) {
			return (getUnscaledWidth() * scale).toInt()
		}
		return getUnscaledWidth()
	}

	fun getEffectiveHeight(): Int {
		if (scaleable) {
			return (getUnscaledHeight() * scale).toInt()
		}
		return getUnscaledHeight()
	}

	fun getEffectivePosition(): Pair<Int, Int> {
		return position
	}

	fun setEffectivePosition(newX: Int, newY: Int) {
		position = newX to newY
	}

	fun applyTransformations(jarvis: Jarvis?, matrices: Matrix3x2f) {
		matrices.translate(position.first.toFloat(), position.second.toFloat())
		if (scaleable) matrices.scale(scale)
	}
}