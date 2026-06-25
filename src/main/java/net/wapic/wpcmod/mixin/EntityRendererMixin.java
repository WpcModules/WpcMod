package net.wapic.wpcmod.mixin;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.wapic.wpcmod.config.components.GlowableESPConfig;
import net.wapic.wpcmod.features.entity.EspCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void onUpdateRenderState(Entity entity, EntityRenderState state, float partialTicks, CallbackInfo ci) {
		GlowableESPConfig config = EspCache.INSTANCE.getOrCompute(entity);
		if (config != null) {
			boolean shouldGlow = config.getGlow();

			if (shouldGlow) {
				int color = config.getColor().getEffectiveColourRGB();
				if (!entity.isCurrentlyGlowing()) {
					state.setData(EspCache.ENTITY_HAS_CUSTOM_GLOW, true);
				}
				state.outlineColor = color;
			}
		}
	}
}