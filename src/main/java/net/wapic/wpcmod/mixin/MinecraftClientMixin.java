package net.wapic.wpcmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.wapic.wpcmod.events.WorldChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

	@Inject(method = "setWorld", at = @At("HEAD"))
	private void world_change_before(ClientWorld world, CallbackInfo ci) {
		if (world != null) {
			WorldChangeEvent.BEFORE.invoker().onWorldChange(world);
		}
	}

	@Inject(method = "setWorld", at = @At("TAIL"))
	private void world_change_after(ClientWorld world, CallbackInfo ci) {
		if (world != null) {
			WorldChangeEvent.AFTER.invoker().onWorldChange(world);
		}
	}
}
