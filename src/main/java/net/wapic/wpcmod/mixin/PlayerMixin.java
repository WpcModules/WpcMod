package net.wapic.wpcmod.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.wapic.wpcmod.features.general.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(method = "isSpectator", at = @At("HEAD"), cancellable = true)
	private void isSpectator(CallbackInfoReturnable<Boolean> cir) {
		if (Freecam.Companion.getCamera() == (Player) (Object) this && Freecam.Companion.isEnabled()) {
			cir.setReturnValue(true);
		}
	}
}
