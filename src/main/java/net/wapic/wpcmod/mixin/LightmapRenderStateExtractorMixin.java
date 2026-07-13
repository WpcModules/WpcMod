package net.wapic.wpcmod.mixin;

import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.client.renderer.state.LightmapRenderState;
import net.wapic.wpcmod.WpcMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapRenderStateExtractor.class)
public class LightmapRenderStateExtractorMixin {
	@Inject(method = "extract", at = @At(value = "RETURN"))
	private void getDimensionAmbientLight(LightmapRenderState renderState, float partialTicks, CallbackInfo ci) {
		if (WpcMod.config.getRender().getFullbright()) {
			renderState.nightVisionEffectIntensity = 1.5F;
		}
	}
}
