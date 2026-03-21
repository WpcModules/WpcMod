package net.wapic.wpcmod.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.effect.MobEffects;
import net.wapic.wpcmod.WpcMod;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

	@Inject(at = @At("HEAD"), method = "setupFog", cancellable = true)
	private static void applyFog(Camera camera, int renderDistance, DeltaTracker deltaTracker, float darkenWorldAmount, ClientLevel level, CallbackInfoReturnable<Vector4f> cir) {
		Minecraft client = Minecraft.getInstance();
		if (client.player != null) {
			boolean isBlind = client.player.hasEffect(MobEffects.BLINDNESS);
			if (WpcMod.config.getRender().getNoBlindness() && isBlind) {
				cir.setReturnValue(new Vector4f(1f, 1f, 1f, 0f));
			}
		}
	}
}