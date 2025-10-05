package net.wapic.wpcmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import net.wapic.wpcmod.features.entity.MobGlow;
import net.wapic.wpcmod.features.general.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;hasOutline(Lnet/minecraft/entity/Entity;)Z"), method = {"getEntitiesToRender", "renderEntities"}, require = 2)
	private boolean shouldMobGlow(boolean original, @Local Entity entity) {
		boolean shouldGlow = MobGlow.INSTANCE.hasOrCompute(entity);
		return shouldGlow || original;
	}

	@Redirect(method = "getEntitiesToRender", require = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;getFocusedEntity()Lnet/minecraft/entity/Entity;", ordinal = 3))
	private Entity freecam_RenderPlayer(Camera camera) {
		if (Freecam.Companion.isEnabled()) {
			return MinecraftClient.getInstance().player;
		}
		return camera.getFocusedEntity();
	}

	@SuppressWarnings({"InvalidInjectorMethodSignature", "MixinAnnotationTarget"})
	@ModifyVariable(method = "renderEntities",
			slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;hasOutline(Lnet/minecraft/entity/Entity;)Z"),
					to = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;setColor(IIII)V")),
			at = @At("STORE"), ordinal = 0
	)
	private int modifyGlowColor(int color, @Local Entity entity) {
		return MobGlow.INSTANCE.getMobGlowOrDefault(entity, color);
	}
}