package net.wapic.wpcmod.mixin;

import net.minecraft.world.level.material.FogType;
import net.minecraft.client.Camera;
import net.wapic.wpcmod.WpcMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {

	@Inject(method = "getFluidInCamera", at = @At("HEAD"), cancellable = true)
	private void disableFluidFog(CallbackInfoReturnable<FogType> cir) {
		if (WpcMod.config.getRender().getDisableFluidFog()) {
			cir.setReturnValue(FogType.NONE);
		}
	}
}
