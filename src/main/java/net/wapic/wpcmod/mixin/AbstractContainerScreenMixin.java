package net.wapic.wpcmod.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
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

	@Inject(at = @At("HEAD"), method = "extractSlot", cancellable = true)
	protected void drawSlot$Before(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
		GuiEvents.DRAW_SLOT_BACKGROUND.invoker().onDrawSlot(graphics, (Screen) (Object) this, slot, ci);
	}

	@Inject(at = @At("HEAD"), method = "slotClicked", cancellable = true)
	private void mouseClicked(Slot slot, int slotId, int buttonNum, ContainerInput containerInput, CallbackInfo ci) {
		GuiEvents.SLOT_CLICKED.invoker().onSlotClick(slot, slotId, buttonNum, containerInput, ci);
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
	private void onMouseClicked(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
		GuiEvents.MOUSE_CLICK.invoker().onMouseClick((Screen) (Object) this, (int) event.x(), (int) event.y(), event.button(), cir);
	}

	@Inject(at = @At("HEAD"), method = "mouseScrolled")
	private void mouseScrolled(double x, double y, double scrollX, double scrollY, CallbackInfoReturnable<Boolean> cir) {
		GuiEvents.MOUSE_SCROLL.invoker().onMouseScroll(x, y, scrollY, scrollX, hoveredSlot);
	}

	@Inject(at = @At("HEAD"), method = "onStopHovering")
	private void resetTooltipSubmenus(Slot slot, CallbackInfo ci) {
		TooltipEvents.RESET.invoker().onTooltipReset();
	}

	@Inject(at = @At("HEAD"), method = "extractTooltip", cancellable = true)
	private void drawMouseOverTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
		TooltipEvents.RENDER.invoker().onRenderTooltip((Screen) (Object) this, mouseX, mouseY, graphics, ci);
	}

	@Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
	private void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
		GuiEvents.RENDER.invoker().onRender((Screen) (Object) this, graphics, mouseX, mouseY, a, ci);
	}

	@Inject(method = "extractContents", at = @At("HEAD"), cancellable = true)
	private void renderBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
		GuiEvents.DRAW_BACKGROUND.invoker().onDrawBackground((Screen) (Object) this, graphics, ci);
	}
}
