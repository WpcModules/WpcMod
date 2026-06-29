package net.wapic.wpcmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.effect.MobEffects;
import net.wapic.wpcmod.WpcMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

	@ModifyReturnValue(method = "setupFog", at = @At("TAIL"))
	private static FogData setupFog(FogData original) {
		Minecraft client = Minecraft.getInstance();
		float customBlindnessOpacity = WpcMod.config.getRender().getBlindnessOpacity();
		if (client.player != null && customBlindnessOpacity < 1.0f) {
			boolean isBlind = client.player.hasEffect(MobEffects.BLINDNESS);
			if (isBlind) {
				original.color.w = customBlindnessOpacity;
				return original;
			}
		}
		return original;
	}
}