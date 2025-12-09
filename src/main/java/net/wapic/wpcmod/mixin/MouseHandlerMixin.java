package net.wapic.wpcmod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import com.mojang.blaze3d.platform.InputConstants;
import net.wapic.wpcmod.features.general.shortcut.Shortcut;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

	@Final
	@Shadow
	private Minecraft minecraft;

	@Inject(at = @At("TAIL"), method = "onButton")
	private void onMouseButton(long window, MouseButtonInfo input, int action, CallbackInfo ci) {
		if (this.minecraft != null && this.minecraft.screen == null && this.minecraft.getOverlay() == null) {
			boolean bl = action == 1;
			Shortcut.Companion.setKeyPressed(InputConstants.Type.MOUSE.getOrCreate(input.button()), bl);
			if (bl) {
				Shortcut.Companion.onKeyPressed(InputConstants.Type.MOUSE.getOrCreate(input.button()));
			}
		}
	}
}
