package net.wapic.wpcmod.mixin;

import net.minecraft.client.CameraType;
import net.minecraft.client.Options;
import net.wapic.wpcmod.WpcMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Options.class)
public abstract class OptionsMixin {

	@ModifyVariable(method = "setCameraType", at = @At("HEAD"), argsOnly = true)
	private CameraType setPerspective(CameraType cameraType) {
		return WpcMod.config.getRender().getDisableFrontCamera() && cameraType.isMirrored() ? CameraType.FIRST_PERSON : cameraType;
	}
}
