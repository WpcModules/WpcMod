package net.wapic.wpcmod.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.wapic.wpcmod.events.GuiEvents;
import net.wapic.wpcmod.events.TooltipEvents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

	@Shadow
	@Nullable
	protected Slot focusedSlot;

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
		if (slot != null) {
			GuiEvents.SLOT_CLICKED.invoker().onSlotClick(slot, slotId, button, slotActionType, ci);
		}
	}

	@Inject(at = @At("HEAD"), method = "mouseScrolled")
	public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
		GuiEvents.MOUSE_SCROLL.invoker().onMouseScroll(mouseX, mouseY, verticalAmount, horizontalAmount, focusedSlot);
	}

	@Inject(at = @At("HEAD"), method = "resetTooltipSubmenus")
	public void resetTooltipSubmenus(Slot slot, CallbackInfo ci) {
		TooltipEvents.RESET.invoker().onTooltipReset();
	}
}
