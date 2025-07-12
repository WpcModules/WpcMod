package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.item.ItemStack
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object ReplaceItemEvent {

    @JvmField
    val EVENT: Event<ReplaceEvent> = EventFactory.createArrayBacked(ReplaceEvent::class.java) { listeners ->
        ReplaceEvent { originalItem, slot, cir ->
            for (listener in listeners) {
                listener.onItemReplaced(originalItem, slot, cir)
            }
        }
    }

    fun interface ReplaceEvent {
        fun onItemReplaced(originalItem: ItemStack, slot: Int, cir: CallbackInfoReturnable<ItemStack>)
    }
}
