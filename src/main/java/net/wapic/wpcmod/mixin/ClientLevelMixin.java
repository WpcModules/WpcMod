package net.wapic.wpcmod.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.wapic.wpcmod.events.BlockEvents;
import net.wapic.wpcmod.events.EntityEvents;
import net.wapic.wpcmod.events.SoundEvents;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin implements BlockGetter {

	@Inject(at = @At("TAIL"), method = "addEntity")
	public void addEntity(Entity entity, CallbackInfo ci) {
		EntityEvents.SPAWN.invoker().onSpawn(entity);
	}

	// https://github.com/SkyblockerMod/Skyblocker/blob/main/src/main/java/de/hysky/skyblocker/mixins/ClientLevelMixin.java
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z"), method = "setServerVerifiedBlockState")
	public void wpcmod$beforeBlockUpdate(BlockPos pos, BlockState blockState, int updateFlag, CallbackInfo ci, @Share("old") LocalRef<BlockState> oldState) {
		oldState.set(getBlockState(pos));
	}

	@Inject(at = @At("RETURN"), method = "setServerVerifiedBlockState")
	public void wpcmod$afterBlockUpdate(BlockPos pos, BlockState blockState, int updateFlag, CallbackInfo ci, @Share("old") LocalRef<BlockState> oldState) {
		BlockEvents.CHANGE.invoker().onChange(pos.immutable(), oldState.get(), blockState);
	}

	@Inject(at = @At("HEAD"), method = "playSeededSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", cancellable = true)
	public void wpcmod$onPlaySeededSound(@Nullable Entity except, double x, double y, double z, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, long seed, CallbackInfo ci) {
		SoundEvents.PLAY.invoker().onPlaySound(sound, source, new Vec3(x, y, z), volume, pitch, seed, (ClientLevel) (Object) this, ci);
	}
}
