package net.wapic.wpcmod.mixin.devenv;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.YggdrasilServicesKeyInfo;
import com.moulberry.mixinconstraints.annotations.IfDevEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfDevEnvironment
@Mixin(value = YggdrasilServicesKeyInfo.class, remap = false)
public class YggdrasilIgnoreSignature {

	@Inject(method = "validateProperty", at = @At("HEAD"), cancellable = true, remap = false)
	public void validate(Property property, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
	}
}
