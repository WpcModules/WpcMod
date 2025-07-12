package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.gui.DrawContext
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object GuiEvents {

    @JvmField
    val DRAW_SLOT_BACKGROUND: Event<DrawSlotBefore> = EventFactory.createArrayBacked(DrawSlotBefore::class.java) { listeners ->
        DrawSlotBefore { drawContext, slot, callbackInfo ->
            for (listener in listeners) {
                listener.onDrawSlot(drawContext, slot, callbackInfo)
            }
        }
    }

    @JvmField
    val DRAW_SLOT_FOREGROUND: Event<DrawSlotAfter> = EventFactory.createArrayBacked(DrawSlotAfter::class.java) { listeners ->
        DrawSlotAfter { drawContext, slot ->
            for (listener in listeners) {
                listener.onDrawSlot(drawContext, slot)
            }
        }
    }

    @JvmField
    val SLOT_CLICKED: Event<SlotClick> = EventFactory.createArrayBacked(SlotClick::class.java) { listeners ->
        SlotClick { slot, slotId, button, slotActionType, callbackInfo ->
            for (listener in listeners) {
                listener.onSlotClick(slot, slotId, button, slotActionType, callbackInfo)
            }
        }
    }

    fun interface DrawSlotBefore {
        fun onDrawSlot(drawContext: DrawContext, slot: Slot, ci: CallbackInfo)
    }

    fun interface DrawSlotAfter {
        fun onDrawSlot(drawContext: DrawContext, slot: Slot)
    }

    fun interface SlotClick {
        fun onSlotClick(slot: Slot, slotId: Int, button: Int, slotActionType: SlotActionType, callbackInfo: CallbackInfo)
    }
}
