package net.wapic.wpcmod.mixin;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import net.wapic.wpcmod.WpcMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {

	@ModifyVariable(method = "setPerspective", at = @At("HEAD"), argsOnly = true)
	private Perspective setPerspective(Perspective value) {
		return WpcMod.config.getRender().getDisableFrontCamera() && value.isFrontView() ? Perspective.FIRST_PERSON : value;
	}
}
