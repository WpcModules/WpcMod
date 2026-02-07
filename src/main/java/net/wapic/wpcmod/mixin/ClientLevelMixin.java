package net.wapic.wpcmod.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.wapic.wpcmod.events.EntityEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {

	@Inject(at = @At("TAIL"), method = "addEntity")
	public void addEntity(Entity entity, CallbackInfo ci) {
		EntityEvents.SPAWN.invoker().onSpawn(entity);
	}
}
