package net.wapic.wpcmod.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.ScrollWheelHandler;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.util.Mth;
import net.wapic.wpcmod.WpcMod;
import net.wapic.wpcmod.features.general.Freecam;
import net.wapic.wpcmod.features.general.shortcut.Shortcut;
import org.joml.Vector2i;
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

	@Shadow
	@Final
	private ScrollWheelHandler scrollWheelHandler;

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

	@Inject(at = @At("HEAD"), method = "onScroll", cancellable = true)
	private void modifyFreecamFlyingSpeed(long windowPointer, double xOffset, double yOffset, CallbackInfo ci) {
		if (Freecam.Companion.isEnabled() && Freecam.Companion.getCamera() != null) {
			double mouseWheelSensitivity = this.minecraft.options.mouseWheelSensitivity().get();
			Vector2i vector2i = this.scrollWheelHandler.onMouseScroll(xOffset * mouseWheelSensitivity, yOffset * mouseWheelSensitivity);

			if (vector2i.x == 0 && vector2i.y == 0) return;

			float flySpeed = Mth.clamp(Freecam.Companion.getCamera().getFlySpeed() + vector2i.y, 0.0F, 40F);
			WpcMod.INSTANCE.getLOGGER().info("Set freecam flying speed: {}", flySpeed);
			Freecam.Companion.getCamera().setFlySpeed(flySpeed);

			ci.cancel();
		}
	}
}
