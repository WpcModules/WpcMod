package net.wapic.wpcmod.mixin;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.wapic.wpcmod.events.GuiEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {

	@Inject(method = "init(II)V", at = @At("TAIL"))
	private void onInit(int width, int height, CallbackInfo ci) {
		Screen screen = (Screen) (Object) this;
		if (screen instanceof ContainerScreen) {
			GuiEvents.BEFORE_OPEN.invoker().onBeforeOpen(screen);
		}
	}
}
