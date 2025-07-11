package net.wapic.wpcmod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.slot.Slot;

public interface GuiEvents {

    Event<DrawSlot> DRAW_SLOT_EVENT = EventFactory.createArrayBacked(DrawSlot.class, (listeners) -> (drawContext, slot) -> {
        for (DrawSlot listener : listeners) {
            listener.onDrawSlot(drawContext, slot);
        }
    });

    interface DrawSlot {
        void onDrawSlot(DrawContext drawContext, Slot slot);
    }
}
