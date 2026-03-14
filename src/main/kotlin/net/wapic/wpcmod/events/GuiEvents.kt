package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
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
		fun onDrawSlot(drawContext: GuiGraphics, screen: Screen, slot: Slot, ci: CallbackInfo)
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
			slot: Slot?, slotId: Int, button: Int, slotActionType: ClickType, callbackInfo: CallbackInfo
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
		fun onRender(screen: Screen, drawContext: GuiGraphics, mouseX: Int, mouseY: Int, deltaTicks: Float, cir: CallbackInfo)
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
		fun onDrawBackground(screen: Screen, drawContext: GuiGraphics, callbackInfo: CallbackInfo)
	}

	@JvmField
	val OPEN: Event<OpenedEvent> = EventFactory.createArrayBacked(OpenedEvent::class.java) { listeners ->
		OpenedEvent { title, containerId ->
			for (listener in listeners) {
				listener.onOpen(title, containerId)
			}
		}
	}

	fun interface OpenedEvent {
		fun onOpen(title: String, containerId: Int)
	}

	@JvmField
	val CLOSE: Event<ClosedEvent> = EventFactory.createArrayBacked(ClosedEvent::class.java) { listeners ->
		ClosedEvent {
			for (listener in listeners) {
				listener.onClose()
			}
		}
	}

	fun interface ClosedEvent {
		fun onClose()
	}

	@JvmField
	val SLOT_UPDATE_AFTER: Event<SlotUpdateAfter> = EventFactory.createArrayBacked(SlotUpdateAfter::class.java) { listeners ->
		SlotUpdateAfter { syncId, slotId, itemStack ->
			for (listener in listeners) {
				listener.onSlotUpdateAfter(syncId, slotId, itemStack)
			}
		}
	}

	fun interface SlotUpdateAfter {
		fun onSlotUpdateAfter(syncId: Int, slotId: Int, itemStack: ItemStack)
	}

	@JvmField
	val SLOT_UPDATE_BEFORE: Event<SlotUpdateBefore> = EventFactory.createArrayBacked(SlotUpdateBefore::class.java) { listeners ->
		SlotUpdateBefore { syncId, slotId, itemStack ->
			for (listener in listeners) {
				listener.onSlotUpdateBefore(syncId, slotId, itemStack)
			}
		}
	}

	fun interface SlotUpdateBefore {
		fun onSlotUpdateBefore(syncId: Int, slotId: Int, itemStack: ItemStack)
	}
}
