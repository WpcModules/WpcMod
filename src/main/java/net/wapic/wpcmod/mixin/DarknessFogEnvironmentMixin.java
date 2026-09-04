package net.wapic.wpcmod.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.DarknessFogEnvironment;
import net.wapic.wpcmod.WpcMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DarknessFogEnvironment.class)
public class DarknessFogEnvironmentMixin {

	@Inject(method = "setupFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F", shift = At.Shift.AFTER))
	private static void setupFog(FogData fog, Camera camera, ClientLevel level, float renderDistance, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (WpcMod.config.getRender().getBlindnessOpacity() < 1.0f) {
			fog.color.w = WpcMod.config.getRender().getDarknessOpacity();
		}
	}
}