package net.wapic.wpcmod.mixin;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.wapic.wpcmod.features.entity.MobGlow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void onUpdateRenderState(Entity entity, EntityRenderState state, float tickProgress, CallbackInfo ci) {
		boolean shouldGlow = MobGlow.INSTANCE.hasOrCompute(entity);
		if (shouldGlow) {
			if (!entity.isCurrentlyGlowing()) {
				state.setData(MobGlow.ENTITY_HAS_CUSTOM_GLOW, true);
			}
			state.outlineColor = MobGlow.INSTANCE.getMobGlowOrDefault(entity, EntityRenderState.NO_OUTLINE);
		}
	}
}
