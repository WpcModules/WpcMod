package net.wapic.wpcmod.features.general

import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.MathHelper
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.GuiEvents
import net.wapic.wpcmod.events.InventoryEvents
import net.wapic.wpcmod.events.TooltipEvents
import org.joml.Vector2i
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import kotlin.math.max

class ScrollableTooltips {

	private val config get() = WpcMod.config.generalConfig.scrollableTooltips

	init {
		GuiEvents.MOUSE_SCROLL.register(::onMouseScroll)
		InventoryEvents.CLOSE.register(::reset)
		TooltipEvents.RESET.register(::reset)
		TooltipEvents.POSITION.register(::onPositionTooltip)
	}

	private fun onMouseScroll(
		mouseX: Double,
		mouseY: Double,
		verticalAmount: Double,
		horizontalAmount: Double,
		focusedSlot: Slot?
	) {
		val scrollAmount = verticalAmount * config.scrollSpeed
		scrolledAmount += scrollAmount.toInt()
	}

	private fun onPositionTooltip(
		screenWidth: Int,
		screenHeight: Int,
		pos: Vector2i,
		width: Int,
		height: Int,
		callbackInfo: CallbackInfo
	) {
		if (height < screenHeight) return

		if (pos.x + width > screenWidth) {
			pos.x = max(pos.x - 24 - width, 4)
		}
		if (config.invertedScroll) {
			scrolledAmount = MathHelper.clamp(scrolledAmount, 0, height)
			pos.y = pos.y - scrolledAmount
		} else {
			scrolledAmount = MathHelper.clamp(scrolledAmount, -height, 0)
			pos.y = pos.y + scrolledAmount
		}


		callbackInfo.cancel()
	}

	private fun reset() {
		scrolledAmount = 0
	}

	companion object {
		var scrolledAmount: Int = 0
	}
}