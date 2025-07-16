package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.item.ItemStack

object InventoryEvents {

    /** When a screen is opened */
    @JvmField
    val OPEN: Event<OpenedEvent> = EventFactory.createArrayBacked(OpenedEvent::class.java) { listeners ->
        OpenedEvent { title ->
            for (listener in listeners) {
                listener.onOpen(title)
            }
        }
    }

    /** Gets Called when a Screen with a new title is opened */
    @JvmField
    val CLOSE: Event<ClosedEvent> = EventFactory.createArrayBacked(ClosedEvent::class.java) { listeners ->
        ClosedEvent {
            for (listener in listeners) {
                listener.onClose()
            }
        }
    }

    /** When a slot in an inventory updates */
    @JvmField
    val SLOT_UPDATE: Event<SlotUpdate> = EventFactory.createArrayBacked(SlotUpdate::class.java) { listeners ->
        SlotUpdate { syncId, slotId, itemStack ->
            for (listener in listeners) {
                listener.onSlotUpdate(syncId, slotId, itemStack)
            }
        }
    }

    /** When an entire inventory gets updated */
    @JvmField
    val UPDATE: Event<UpdateEvent> = EventFactory.createArrayBacked(UpdateEvent::class.java) { listeners ->
        UpdateEvent { syncId, itemStacks, cursorStack ->
            for (listener in listeners) {
                listener.onUpdate(syncId, itemStacks, cursorStack)
            }
        }
    }


    fun interface OpenedEvent {
        fun onOpen(title: String)
    }

    fun interface ClosedEvent {
        fun onClose()
    }

    fun interface SlotUpdate {
        fun onSlotUpdate(syncId: Int, slotId: Int, itemStack: ItemStack)
    }

    fun interface UpdateEvent {
        fun onUpdate(syncId: Int, inventory: List<ItemStack>, cursorStack: ItemStack?)
    }
}
