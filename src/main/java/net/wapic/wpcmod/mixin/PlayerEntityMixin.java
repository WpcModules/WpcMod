package net.wapic.wpcmod.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.wapic.wpcmod.features.general.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "isSpectator", at = @At("HEAD"), cancellable = true)
	private void isSpectator(CallbackInfoReturnable<Boolean> cir) {
		if (Freecam.Companion.getCamera() == (PlayerEntity) (Object) this && Freecam.Companion.isEnabled()) {
			cir.setReturnValue(true);
		}
	}
}
