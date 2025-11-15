package net.wapic.wpcmod.hud

import com.google.gson.annotations.Expose
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.modIdentifier
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
	open val isActive = false

	init {
		val identifier = modIdentifier(label.lowercase(Locale.US).replace(" ", "_"))
		HudLayerRegistrationCallback.EVENT.register { wrapper ->
			wrapper.attachLayerBefore(IdentifiedLayer.DEMO_TIMER, IdentifiedLayer.of(identifier, ::render))
		}
	}

	open fun render(drawContext: DrawContext, tickCounter: RenderTickCounter) {}

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

	fun applyTransformations(matrices: MatrixStack) {
		matrices.translate(getAbsoluteX(), getAbsoluteY(), 0f)
		if (canScale) matrices.scale(scale, scale, 0f)
	}
}