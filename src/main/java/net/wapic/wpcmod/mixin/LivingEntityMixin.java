package net.wapic.wpcmod.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.wapic.wpcmod.events.EntityEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	@Inject(at = @At("HEAD"), method = "handleEntityEvent")
	public void onEntityDeath(byte id, CallbackInfo ci) {
		if (id == 3) {
			EntityEvents.DEATH.invoker().onEntityDeath((LivingEntity) (Object) this);
		}
	}
}
