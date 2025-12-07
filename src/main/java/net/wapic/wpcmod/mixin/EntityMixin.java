package net.wapic.wpcmod.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.wapic.wpcmod.features.general.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Inject(method = "turn", at = @At("HEAD"), cancellable = true)
	private void overrideYaw(double yawChange, double pitchChange, CallbackInfo ci) {
		if (Freecam.Companion.isEnabled() && (Object) this instanceof LocalPlayer) {
			Freecam.Companion.updateCameraRotations((float) yawChange, (float) pitchChange);
			ci.cancel();
		}
	}
}
