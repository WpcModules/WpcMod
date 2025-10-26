package net.wapic.wpcmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.wapic.wpcmod.WpcMod;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

	@Inject(at = @At("HEAD"), method = "applyFog(Lnet/minecraft/client/render/Camera;IZLnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;", cancellable = true)
	private static void applyFog(Camera camera, int viewDistance, boolean thick, RenderTickCounter tickCounter, float skyDarkness, ClientWorld world, CallbackInfoReturnable<Vector4f> cir) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player != null) {
			boolean isBlind = client.player.hasStatusEffect(StatusEffects.BLINDNESS);
			if (WpcMod.config.getRender().getNoBlindness() && isBlind) {
				cir.setReturnValue(new Vector4f(1f, 1f, 1f, 0f)); // TODO: Fix
			}
		}
	}
}