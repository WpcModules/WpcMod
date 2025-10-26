package net.wapic.wpcmod.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.InputUtil;
import net.wapic.wpcmod.features.general.shortcut.Shortcut;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(at = @At("TAIL"), method = "onKey")
	private void onKey(long window, int action, KeyInput input, CallbackInfo ci) {
		if (this.client.currentScreen == null) {
			if (action == 0) {
				Shortcut.Companion.setKeyPressed(InputUtil.Type.KEYSYM.createFromCode(input.key()), false);
			} else {
				Shortcut.Companion.setKeyPressed(InputUtil.Type.KEYSYM.createFromCode(input.key()), true);
				Shortcut.Companion.onKeyPressed(InputUtil.Type.KEYSYM.createFromCode(input.key()));
			}
		}
	}
}
