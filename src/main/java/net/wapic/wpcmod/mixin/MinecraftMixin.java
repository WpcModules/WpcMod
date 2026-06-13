package net.wapic.wpcmod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.wapic.wpcmod.events.PlayerPickEvents;
import net.wapic.wpcmod.events.WorldChangeEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

	@Shadow
	@Nullable
	public HitResult hitResult;

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

	@Inject(method = "pickBlockOrEntity", at = @At("HEAD"), cancellable = true)
	private void onPickBlock(CallbackInfo ci) {
		if (this.hitResult != null && this.hitResult.getType() != HitResult.Type.MISS) {
			switch (this.hitResult) {
				case BlockHitResult blockHitResult:
					PlayerPickEvents.BLOCK.invoker().onPickBlock(blockHitResult.getBlockPos(), ci);
					break;
				case EntityHitResult entityHitResult:
					PlayerPickEvents.ENTITY.invoker().onPickEntity(entityHitResult.getEntity(), entityHitResult.getLocation(), ci);
					break;
				default:
			}
		}
	}
}
