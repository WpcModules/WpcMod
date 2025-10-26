package net.wapic.wpcmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.wapic.wpcmod.features.entity.MobGlow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

	@ModifyExpressionValue(method = "updateRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;hasOutline(Lnet/minecraft/entity/Entity;)Z"))
	private boolean shouldMobGlow(boolean original, @Local(argsOnly = true) Entity entity) {
		boolean shouldGlow = MobGlow.INSTANCE.hasOrCompute(entity);
		return shouldGlow || original;
	}

	@ModifyExpressionValue(method = "updateRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/ColorHelper;fullAlpha(I)I"))
	private int modifyGlowColor(int color, @Local(argsOnly = true) Entity entity) {
		return MobGlow.INSTANCE.getMobGlowOrDefault(entity, color);
	}
}