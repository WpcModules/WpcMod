package net.wapic.wpcmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.entity.effect.StatusEffects;
import net.wapic.wpcmod.WpcMod;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

	@Inject(at = @At("HEAD"), method = "applyFog", cancellable = true)
	private static void applyFog(Camera camera, FogType fogType, Vector4f color, float viewDistance, boolean thickenFog, float tickProgress, CallbackInfoReturnable<Fog> cir) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player != null) {
			boolean isBlind = client.player.hasStatusEffect(StatusEffects.BLINDNESS);
			if (WpcMod.config.getGeneralConfig().getNoBlindness() && isBlind) {
				cir.setReturnValue(Fog.DUMMY);
			}
		}
	}
}