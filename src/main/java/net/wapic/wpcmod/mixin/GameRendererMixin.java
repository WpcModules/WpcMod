package net.wapic.wpcmod.mixin;

import net.minecraft.client.renderer.GameRenderer;
import net.wapic.wpcmod.util.render.WpcModRenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Inject(method = "close", at = @At("TAIL"))
	private void wpcmod$closeRenderer(CallbackInfo ci) {
		WpcModRenderSystem.INSTANCE.close();
	}
}
