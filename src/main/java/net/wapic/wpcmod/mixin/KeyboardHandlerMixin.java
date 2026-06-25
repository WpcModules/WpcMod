package net.wapic.wpcmod.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.wapic.wpcmod.features.general.shortcut.Shortcut;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(at = @At("TAIL"), method = "keyPress")
	private void onKey(long handle, int action, KeyEvent event, CallbackInfo ci) {
		if (this.minecraft.screen == null) {
			if (action == 0) {
				Shortcut.Companion.setKeyPressed(InputConstants.Type.KEYSYM.getOrCreate(event.key()), false);
			} else {
				Shortcut.Companion.setKeyPressed(InputConstants.Type.KEYSYM.getOrCreate(event.key()), true);
				Shortcut.Companion.onKeyPressed(InputConstants.Type.KEYSYM.getOrCreate(event.key()));
			}
		}
	}
}
