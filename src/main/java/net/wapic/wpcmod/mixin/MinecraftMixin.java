package net.wapic.wpcmod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.wapic.wpcmod.events.WorldChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

	@Inject(method = "updateLevelInEngines", at = @At("HEAD"))
	private void world_change_before(ClientLevel world, CallbackInfo ci) {
		if (world != null) {
			WorldChangeEvent.BEFORE.invoker().onWorldChange(world);
		}
	}

	@Inject(method = "updateLevelInEngines", at = @At("TAIL"))
	private void world_change_after(ClientLevel world, CallbackInfo ci) {
		if (world != null) {
			WorldChangeEvent.AFTER.invoker().onWorldChange(world);
		}
	}
}
