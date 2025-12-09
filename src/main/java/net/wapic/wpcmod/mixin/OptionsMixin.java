package net.wapic.wpcmod.mixin;

import net.minecraft.client.Options;
import net.minecraft.client.CameraType;
import net.wapic.wpcmod.WpcMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Options.class)
public abstract class OptionsMixin {

	@ModifyVariable(method = "setCameraType", at = @At("HEAD"), argsOnly = true)
	private CameraType setPerspective(CameraType value) {
		return WpcMod.config.getRender().getDisableFrontCamera() && value.isMirrored() ? CameraType.FIRST_PERSON : value;
	}
}
