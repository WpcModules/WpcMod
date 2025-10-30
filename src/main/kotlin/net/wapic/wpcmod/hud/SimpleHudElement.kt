package net.wapic.wpcmod.hud

import com.google.gson.annotations.Expose
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.modIdentifier
import org.joml.Matrix3x2f
import java.util.*

open class SimpleHudElement(
	@Expose
	var label: String,
	var width: Int,
	var height: Int,
	var canScale: Boolean = true,
) {
	@Expose var scale = 1f
	@Expose var x = 0f
	@Expose var y = 0f
	open val isEnabled = false

	init {
		val identifier = modIdentifier(label.lowercase(Locale.US).replace(" ", "_"))
		HudElementRegistry.attachElementBefore(VanillaHudElements.DEMO_TIMER, identifier) { context, tickCounter ->
			render(context, tickCounter.dynamicDeltaTicks)
		}
		HudManager.registerElement(this)
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

	fun getAbsoluteX(): Float {
		return x * (MC.window.scaledWidth - getEffectiveWidth())
	}

	fun getAbsoluteY(): Float {
		return y * (MC.window.scaledHeight - getEffectiveHeight())
	}

	fun applyTransformations(matrices: Matrix3x2f) {
		matrices.translate(getAbsoluteX(), getAbsoluteY())
		if (canScale) matrices.scale(scale)
	}
}