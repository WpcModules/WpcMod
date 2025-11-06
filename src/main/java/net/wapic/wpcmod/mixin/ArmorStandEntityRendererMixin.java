package net.wapic.wpcmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.entity.ArmorStandEntityRenderer;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.wapic.wpcmod.features.entity.MobGlow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArmorStandEntityRenderer.class)
public class ArmorStandEntityRendererMixin {
	@ModifyExpressionValue(method = "updateRenderState(Lnet/minecraft/entity/decoration/ArmorStandEntity;Lnet/minecraft/client/render/entity/state/ArmorStandEntityRenderState;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/decoration/ArmorStandEntity;isMarker()Z"))
	private boolean glowOnlyVisibleParts(boolean isMarker, @Local(argsOnly = true) ArmorStandEntityRenderState renderState) {
		return renderState.getDataOrDefault(MobGlow.ENTITY_HAS_CUSTOM_GLOW, false) || isMarker;
	}
}
