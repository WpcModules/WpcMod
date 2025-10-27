package net.wapic.wpcmod.hud

import com.google.gson.annotations.Expose
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.wapic.wpcmod.util.Utils.modIdentifier
import org.joml.Matrix3x2f
import java.awt.Point
import java.util.*

open class SimpleHudElement(
	@Expose
	var label: String,
	var width: Int,
	var height: Int,
	var canScale: Boolean = true,
) {
	@Expose
	var scale = 1f
	@Expose
	var position = Point(0, 0)

	init {
		val identifier = modIdentifier(label.lowercase(Locale.US).replace(" ", "_"))
		HudElementRegistry.attachElementBefore(VanillaHudElements.DEMO_TIMER, identifier) { context, tickCounter ->
			render(context, tickCounter.dynamicDeltaTicks)
		}
	}

	open fun render(drawContext: DrawContext, deltaTicks: Float) {}

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
		if (canScale) {
			return (getUnscaledWidth() * scale).toInt()
		}
		return getUnscaledWidth()
	}

	fun getEffectiveHeight(): Int {
		if (canScale) {
			return (getUnscaledHeight() * scale).toInt()
		}
		return getUnscaledHeight()
	}

	fun getEffectivePosition(): Point {
		return position
	}

	fun setEffectivePosition(newX: Int, newY: Int) {
		position = Point(newX, newY)
	}

	fun applyTransformations(matrices: Matrix3x2f) {
		matrices.translate(position.x.toFloat(), position.y.toFloat())
		if (canScale) matrices.scale(scale)
	}
}