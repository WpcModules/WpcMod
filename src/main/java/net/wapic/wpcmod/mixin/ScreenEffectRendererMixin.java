package net.wapic.wpcmod.mixin;

import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.util.ARGB;
import net.wapic.wpcmod.WpcMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {

	@ModifyArgs(method = "buildFireQuad", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ScreenEffectRenderer;buildSpriteQuad(Lcom/mojang/blaze3d/vertex/VertexConsumer;Lorg/joml/Matrix4f;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;FFFFFI)V"))
	private static void wpcmod$disableFireOverlay(Args args) {
		float height = WpcMod.config.getRender().getFlameOverlay().getFlameOverlayHeight() / 2f;
		int originalColor = args.get(8);
		int originalAlpha = ARGB.alpha(originalColor);
		int newColor = ARGB.color((WpcMod.config.getRender().getFlameOverlay().getFlameOverlayOpacity() * originalAlpha) / 255f, originalColor);
		args.set(4, height - 1F);
		args.set(6, height);
		args.set(8, newColor);
	}

}
