package net.wapic.wpcmod.mixin;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.wapic.wpcmod.features.entity.MobGlow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

	@Inject(method = "updateRenderState", at = @At("TAIL"))
	private void onUpdateRenderState(Entity entity, EntityRenderState state, float tickProgress, CallbackInfo ci) {
		boolean shouldGlow = MobGlow.INSTANCE.hasOrCompute(entity);
		if (shouldGlow) {
			if (!entity.isGlowing()) {
				state.setData(MobGlow.ENTITY_HAS_CUSTOM_GLOW, true);
			}
			state.outlineColor = MobGlow.INSTANCE.getMobGlowOrDefault(entity, EntityRenderState.NO_OUTLINE);
		}
	}
}
