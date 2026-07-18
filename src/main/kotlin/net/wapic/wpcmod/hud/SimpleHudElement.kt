package net.wapic.wpcmod.hud

import com.google.gson.annotations.Expose
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.MC
import org.joml.Matrix3x2f
import java.util.*

open class SimpleHudElement(
	@Expose
	var label: String,
	@Expose
	var width: Int,
	@Expose
	var height: Int,
	var canScale: Boolean = true,
	var defaultScale: Float = 1f,
	var defaultX: Float = 0f,
	var defaultY: Float = 0f,
) {
	@Expose var scale = defaultScale
	@Expose var x = defaultX
	@Expose var y = defaultY
	open val isEnabled = false
	open val isActive = false

	init {
		val identifier = WpcMod.Identifier(label.lowercase(Locale.US).replace(" ", "_"))
		HudElementRegistry.attachElementBefore(VanillaHudElements.DEMO_TIMER, identifier) { context, tickCounter ->
			render(context, tickCounter.getGameTimeDeltaPartialTick(true))
		}
	}

	open fun render(drawContext: GuiGraphicsExtractor, deltaTicks: Float) {}

	fun setDimensions(width: Int, height: Int) {
		this.width = width
		this.height = height
	}

	fun getLabel(): Component {
		return Component.nullToEmpty(label)
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
		return x * (MC.window.guiScaledWidth - getEffectiveWidth())
	}

	fun getAbsoluteY(): Float {
		return y * (MC.window.guiScaledHeight - getEffectiveHeight())
	}

	fun applyTransformations(matrices: Matrix3x2f) {
		matrices.translate(getAbsoluteX(), getAbsoluteY())
		if (canScale) matrices.scale(scale)
	}
}