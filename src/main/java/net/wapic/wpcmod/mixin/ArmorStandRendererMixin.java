package net.wapic.wpcmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.wapic.wpcmod.features.entity.MobGlow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArmorStandRenderer.class)
public class ArmorStandRendererMixin {
	@ModifyExpressionValue(method = "extractRenderState(Lnet/minecraft/world/entity/decoration/ArmorStand;Lnet/minecraft/client/renderer/entity/state/ArmorStandRenderState;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/ArmorStand;isMarker()Z"))
	private boolean glowOnlyVisibleParts(boolean isMarker, @Local(argsOnly = true) ArmorStandRenderState renderState) {
		return renderState.getDataOrDefault(MobGlow.ENTITY_HAS_CUSTOM_GLOW, false) || isMarker;
	}
}
