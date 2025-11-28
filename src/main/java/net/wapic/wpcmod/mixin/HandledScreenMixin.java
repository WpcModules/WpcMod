package net.wapic.wpcmod.mixin;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.wapic.wpcmod.events.GuiEvents;
import net.wapic.wpcmod.events.TooltipEvents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
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

	@Shadow
	@Final
	protected Text playerInventoryTitle;

	@Inject(at = @At("HEAD"), method = "drawSlot", cancellable = true)
	protected void drawSlot$Before(DrawContext context, Slot slot, CallbackInfo ci) {
		GuiEvents.DRAW_SLOT_BACKGROUND.invoker().onDrawSlot(context, (Screen) (Object) this, slot, ci);
	}

	@Inject(at = @At("HEAD"), method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", cancellable = true)
	private void mouseClicked(Slot slot, int slotId, int button, SlotActionType slotActionType, CallbackInfo ci) {
		GuiEvents.SLOT_CLICKED.invoker().onSlotClick(slot, slotId, button, slotActionType, ci);
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
	private void onMouseClicked(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
		GuiEvents.MOUSE_CLICK.invoker().onMouseClick((Screen) (Object) this, (int) click.x(), (int) click.y(), click.button(), cir);
	}

	@Inject(at = @At("HEAD"), method = "mouseScrolled")
	private void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
		GuiEvents.MOUSE_SCROLL.invoker().onMouseScroll(mouseX, mouseY, verticalAmount, horizontalAmount, focusedSlot);
	}

	@Inject(at = @At("HEAD"), method = "resetTooltipSubmenus")
	private void resetTooltipSubmenus(Slot slot, CallbackInfo ci) {
		TooltipEvents.RESET.invoker().onTooltipReset();
	}

	@Inject(at = @At("HEAD"), method = "drawMouseoverTooltip", cancellable = true)
	private void drawMouseOverTooltip(DrawContext drawContext, int x, int y, CallbackInfo ci) {
		TooltipEvents.RENDER.invoker().onRenderTooltip((Screen) (Object) this, x, y, drawContext, ci);
	}
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void render(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
		GuiEvents.RENDER.invoker().onRender((Screen) (Object) this, context, mouseX, mouseY, deltaTicks, ci);
	}

	@Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
	private void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
		GuiEvents.DRAW_BACKGROUND.invoker().onDrawBackground((Screen) (Object) this, context, ci);
	}

	@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
	private void renderBackground(KeyInput input, CallbackInfoReturnable<Boolean> cir) {
		GuiEvents.KEY_PRESSED.invoker().onKeyPressed(input, cir);
	}
}
