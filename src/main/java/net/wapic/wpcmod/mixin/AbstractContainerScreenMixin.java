package net.wapic.wpcmod.mixin;

import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ClickType;
import net.wapic.wpcmod.events.GuiEvents;
import net.wapic.wpcmod.events.TooltipEvents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

	@Shadow
	@Nullable
	protected Slot hoveredSlot;

	@Inject(at = @At("HEAD"), method = "renderSlot", cancellable = true)
	protected void drawSlot$Before(GuiGraphics context, Slot slot, CallbackInfo ci) {
		GuiEvents.DRAW_SLOT_BACKGROUND.invoker().onDrawSlot(context, (Screen) (Object) this, slot, ci);
	}

	@Inject(at = @At("HEAD"), method = "slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ClickType;)V", cancellable = true)
	private void mouseClicked(Slot slot, int slotId, int button, ClickType slotActionType, CallbackInfo ci) {
		GuiEvents.SLOT_CLICKED.invoker().onSlotClick(slot, slotId, button, slotActionType, ci);
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
	private void onMouseClicked(MouseButtonEvent click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
		GuiEvents.MOUSE_CLICK.invoker().onMouseClick((Screen) (Object) this, (int) click.x(), (int) click.y(), click.button(), cir);
	}

	@Inject(at = @At("HEAD"), method = "mouseScrolled")
	private void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
		GuiEvents.MOUSE_SCROLL.invoker().onMouseScroll(mouseX, mouseY, verticalAmount, horizontalAmount, hoveredSlot);
	}

	@Inject(at = @At("HEAD"), method = "onStopHovering")
	private void resetTooltipSubmenus(Slot slot, CallbackInfo ci) {
		TooltipEvents.RESET.invoker().onTooltipReset();
	}

	@Inject(at = @At("HEAD"), method = "renderTooltip", cancellable = true)
	private void drawMouseOverTooltip(GuiGraphics drawContext, int x, int y, CallbackInfo ci) {
		TooltipEvents.RENDER.invoker().onRenderTooltip((Screen) (Object) this, x, y, drawContext, ci);
	}
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
		GuiEvents.RENDER.invoker().onRender((Screen) (Object) this, context, mouseX, mouseY, deltaTicks, ci);
	}

	@Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
	private void renderBackground(GuiGraphics context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
		GuiEvents.DRAW_BACKGROUND.invoker().onDrawBackground((Screen) (Object) this, context, ci);
	}
}
