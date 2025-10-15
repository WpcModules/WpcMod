package net.wapic.wpcmod.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.wapic.wpcmod.events.GuiEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GenericContainerScreen.class)
public class GenericContainerScreenMixin {

	@Inject(method = "drawBackground", at = @At("RETURN"))
	protected void onDrawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY, CallbackInfo ci) {
		GuiEvents.DRAW_BACKGROUND.invoker().onDrawBackground((Screen) (Object) this, context);
	}
}
