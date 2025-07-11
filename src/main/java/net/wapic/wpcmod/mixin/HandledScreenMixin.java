package net.wapic.wpcmod.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.wapic.wpcmod.events.GuiEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    @Inject(at = @At("HEAD"), method = "drawSlot")
    protected void drawSlot(DrawContext context, Slot slot, CallbackInfo ci) {
        GuiEvents.DRAW_SLOT_EVENT.invoker().onDrawSlot(context, slot);
    }
}
