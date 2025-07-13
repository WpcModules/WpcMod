package net.wapic.wpcmod.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.wapic.wpcmod.events.GuiEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    @Inject(at = @At("HEAD"), method = "drawSlot", cancellable = true)
    protected void drawSlot$Before(DrawContext context, Slot slot, CallbackInfo ci) {
        GuiEvents.DRAW_SLOT_BACKGROUND.invoker().onDrawSlot(context, slot, ci);
    }

    @Inject(at = @At("TAIL"), method = "drawSlot")
    protected void drawSlot$After(DrawContext context, Slot slot, CallbackInfo ci) {
        GuiEvents.DRAW_SLOT_FOREGROUND.invoker().onDrawSlot(context, slot);
    }

    @Inject(at = @At("HEAD"), method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V")
    private void mouseClicked(Slot slot, int slotId, int button, SlotActionType slotActionType, CallbackInfo ci) {
        if(slot != null) {
            GuiEvents.SLOT_CLICKED.invoker().onSlotClick(slot, slotId, button, slotActionType, ci);
        }
    }
}
