package net.wapic.wpcmod.config.components.slider

import io.github.notenoughupdates.moulconfig.GuiTextures
import io.github.notenoughupdates.moulconfig.common.IMinecraft
import io.github.notenoughupdates.moulconfig.common.KeyboardConstants
import io.github.notenoughupdates.moulconfig.common.text.StructuredText
import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent
import io.github.notenoughupdates.moulconfig.gui.MouseEvent
import io.github.notenoughupdates.moulconfig.observer.GetSetter
import net.minecraft.util.math.MathHelper.clamp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class SliderComponentWithText(
	val value: GetSetter<Float>,
	val minValue: Float,
	val maxValue: Float,
	val minStep: Float
) : GuiComponent() {
	var clicked: Boolean = false

	override fun getWidth(): Int {
		return 64
	}

	override fun getHeight(): Int {
		return 28
	}

	override fun render(context: GuiImmediateContext) {
		if (clicked) {
			setValueFromContext(context)
		}

		val fr = IMinecraft.INSTANCE.defaultFontRenderer
		val sliderHeight = (height / 2f) - 2f

		context.renderContext.drawTexturedRect(
			GuiTextures.TOGGLE_BAR,
			0f,
			sliderHeight,
			context.width.toFloat(),
			sliderHeight
		)

		val value: Float = value.get()
		val sliderPosition =
			((value.coerceIn(minValue..maxValue) - minValue) / (maxValue - minValue) * context.width).toInt()

		context.renderContext.drawTexturedRect(
			GuiTextures.SLIDER_BUTTON,
			sliderPosition - 4f,
			sliderHeight,
			8f,
			sliderHeight,
		)

		val text = StructuredText.of(value.toString().removeSuffix(".0"))
		context.renderContext.drawStringCenteredScaledMaxWidth(
			text,
			fr,
			sliderPosition.toFloat(),
			sliderHeight - 4f,
			true,
			16,
			0xAEFBAD
		)
	}

	fun setValueFromContext(context: GuiImmediateContext) {
		var v: Float = context.mouseX * (maxValue - minValue) / (context.width) + minValue
		v = min(v.toDouble(), maxValue.toDouble()).toFloat()
		v = max(v.toDouble(), minValue.toDouble()).toFloat()
		v = (v / minStep).roundToInt() * minStep
		value.set(v)
	}

	override fun mouseEvent(mouseEvent: MouseEvent, context: GuiImmediateContext): Boolean {
		if (!context.renderContext.isMouseButtonDown(0)) clicked = false
		if (context.isHovered && mouseEvent is MouseEvent.Click && mouseEvent.mouseState && mouseEvent.mouseButton == 0) {
			clicked = true
		}
		if (clicked) {
			setValueFromContext(context)
			return true
		}
		return false
	}

	override fun keyboardEvent(event: KeyboardEvent, context: GuiImmediateContext): Boolean {
		if (event is KeyboardEvent.KeyPressed && context.isHovered && event.pressed) {
			when (event.keycode) {
				KeyboardConstants.left -> {
					value.set(clamp(value.get() - minStep, minValue, maxValue))
					return true
				}

				KeyboardConstants.right -> {
					value.set(clamp(value.get() + minStep, minValue, maxValue))
					return true
				}
			}
		}
		return super.keyboardEvent(event, context)
	}
}