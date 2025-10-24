package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object GuiEvents {

	@JvmField
	val DRAW_SLOT_BACKGROUND: Event<DrawSlotBefore> =
		EventFactory.createArrayBacked(DrawSlotBefore::class.java) { listeners ->
			DrawSlotBefore { drawContext, screen, slot, callbackInfo ->
				for (listener in listeners) {
					listener.onDrawSlot(drawContext, screen, slot, callbackInfo)
				}
			}
		}

	fun interface DrawSlotBefore {
		fun onDrawSlot(drawContext: DrawContext, screen: Screen, slot: Slot, ci: CallbackInfo)
	}

	@JvmField
	val SLOT_CLICKED: Event<SlotClick> = EventFactory.createArrayBacked(SlotClick::class.java) { listeners ->
		SlotClick { slot, slotId, button, slotActionType, callbackInfo ->
			for (listener in listeners) {
				listener.onSlotClick(slot, slotId, button, slotActionType, callbackInfo)
			}
		}
	}

	fun interface SlotClick {
		fun onSlotClick(
			slot: Slot?, slotId: Int, button: Int, slotActionType: SlotActionType, callbackInfo: CallbackInfo
		)
	}

	@JvmField
	val MOUSE_SCROLL: Event<MouseScroll> = EventFactory.createArrayBacked(MouseScroll::class.java) { listeners ->
		MouseScroll { mouseX, mouseY, verticalAmount, horizontalAmount, focusedSlot ->
			for (listener in listeners) {
				listener.onMouseScroll(mouseX, mouseY, verticalAmount, horizontalAmount, focusedSlot)
			}
		}
	}

	fun interface MouseScroll {
		fun onMouseScroll(
			mouseX: Double, mouseY: Double, verticalAmount: Double, horizontalAmount: Double, focusedSlot: Slot?
		)
	}

	@JvmField
	val MOUSE_CLICK: Event<MouseClick> = EventFactory.createArrayBacked(MouseClick::class.java) { listeners ->
		MouseClick { screen, mouseX, mouseY, button, cir ->
			for (listener in listeners) {
				listener.onMouseClick(screen, mouseX, mouseY, button, cir)
			}
		}
	}

	fun interface MouseClick {
		fun onMouseClick(screen: Screen, mouseX: Int, mouseY: Int, button: Int, cir: CallbackInfoReturnable<Boolean>)
	}

	@JvmField
	val RENDER: Event<Render> = EventFactory.createArrayBacked(Render::class.java) { listeners ->
		Render { screen, context, mouseX, mouseY, deltaTicks, cir ->
			for (listener in listeners) {
				listener.onRender(screen, context, mouseX, mouseY, deltaTicks, cir)
			}
		}
	}

	fun interface Render {
		fun onRender(screen: Screen, drawContext: DrawContext, mouseX: Int, mouseY: Int, deltaTicks: Float, cir: CallbackInfo)
	}

	@JvmField
	val DRAW_BACKGROUND: Event<DrawBackground> = EventFactory.createArrayBacked(DrawBackground::class.java) { listeners ->
		DrawBackground { screen, context, callbackInfo ->
			for (listener in listeners) {
				listener.onDrawBackground(screen, context, callbackInfo)
			}
		}
	}

	fun interface DrawBackground {
		fun onDrawBackground(screen: Screen, drawContext: DrawContext, callbackInfo: CallbackInfo)
	}
}
