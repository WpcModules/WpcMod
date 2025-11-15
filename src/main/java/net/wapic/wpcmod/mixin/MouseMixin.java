package net.wapic.wpcmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.InputUtil;
import net.wapic.wpcmod.features.general.shortcut.Shortcut;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

	@Final
	@Shadow
	private MinecraftClient client;

	@Inject(at = @At("TAIL"), method = "onMouseButton")
	private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
		if (this.client != null && this.client.currentScreen == null && this.client.getOverlay() == null) {
			boolean bl = action == 1;
			Shortcut.Companion.setKeyPressed(InputUtil.Type.MOUSE.createFromCode(button), bl);
			if (bl) {
				Shortcut.Companion.onKeyPressed(InputUtil.Type.MOUSE.createFromCode(button));
			}
		}
	}
}
