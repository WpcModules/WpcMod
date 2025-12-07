package net.wapic.wpcmod.features.inventory

import net.minecraft.world.inventory.Slot
import net.minecraft.util.Mth
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.GuiEvents
import net.wapic.wpcmod.events.InventoryEvents
import net.wapic.wpcmod.events.TooltipEvents
import org.joml.Vector2i
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import kotlin.math.max

object ScrollableTooltips {

	private val config get() = WpcMod.config.inventory.scrollableTooltips
	private var scrolledAmount: Int = 0

	fun init() {
		GuiEvents.MOUSE_SCROLL.register(::onMouseScroll)
		InventoryEvents.CLOSE.register(::reset)
		TooltipEvents.RESET.register(::reset)
		TooltipEvents.POSITION.register(::onPositionTooltip)
	}

	private fun onMouseScroll(mouseX: Double, mouseY: Double, verticalAmount: Double, horizontalAmount: Double, focusedSlot: Slot?) {
		val scrollAmount = verticalAmount * config.scrollSpeed
		scrolledAmount += scrollAmount.toInt()
	}

	private fun onPositionTooltip(
		screenWidth: Int, screenHeight: Int, pos: Vector2i, width: Int, height: Int, callbackInfo: CallbackInfo
	) {
		if (height < screenHeight) return

		if (pos.x + width > screenWidth) {
			pos.x = max(pos.x - 24 - width, 4)
		}
		if (config.invertedScroll) {
			scrolledAmount = Mth.clamp(scrolledAmount, 0, height)
			pos.y -= scrolledAmount
		} else {
			scrolledAmount = Mth.clamp(scrolledAmount, -height, 0)
			pos.y += scrolledAmount
		}


		callbackInfo.cancel()
	}

	private fun reset() {
		scrolledAmount = 0
	}
}