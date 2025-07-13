package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.item.ItemStack

object InventoryEvents {

    @JvmField
    val OPEN: Event<OpenedEvent> = EventFactory.createArrayBacked(OpenedEvent::class.java) { listeners ->
        OpenedEvent { title ->
            for (listener in listeners) {
                listener.onOpen(title)
            }
        }
    }

    /**
     * Gets Called when a Screen with a new title is opened
     */
    @JvmField
    val CLOSE: Event<ClosedEvent> = EventFactory.createArrayBacked(ClosedEvent::class.java) { listeners ->
        ClosedEvent {
            for (listener in listeners) {
                listener.onClose()
            }
        }
    }

    @JvmField
    val UPDATE: Event<UpdateEvent> = EventFactory.createArrayBacked(UpdateEvent::class.java) { listeners ->
        UpdateEvent { syncId, slotId, itemStack ->
            for (listener in listeners) {
                listener.onUpdate(syncId, slotId, itemStack)
            }
        }
    }


    fun interface OpenedEvent {
        fun onOpen(title: String)
    }

    fun interface ClosedEvent {
        fun onClose()
    }

    fun interface UpdateEvent {
        fun onUpdate(syncId: Int, slotId: Int, itemStack: ItemStack)
    }
}
