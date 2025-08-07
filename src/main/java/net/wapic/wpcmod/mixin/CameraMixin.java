package net.wapic.wpcmod.mixin;

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.wapic.wpcmod.WpcMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {

	@Inject(method = "getSubmersionType", at = @At("HEAD"), cancellable = true)
	private void disableFluidFog(CallbackInfoReturnable<CameraSubmersionType> cir) {
		if (WpcMod.config.getRender().getDisableFluidFog()) {
			cir.setReturnValue(CameraSubmersionType.NONE);
		}
	}
}
