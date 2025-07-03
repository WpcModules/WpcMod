package net.wapic.wpcmod.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.wapic.wpcmod.events.PacketEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

	@Shadow
    private ClientWorld world;

	@Inject(at = @At("HEAD"), method = "onParticle")
	private void onParticle(ParticleS2CPacket packet, CallbackInfo ci) {
		PacketEvents.PARTICLE.invoker().onParticle(packet, world);
	}

	@Inject(at = @At("HEAD"), method = "onPlaySound")
	private void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
		PacketEvents.PLAY_SOUND.invoker().onPlaySound(packet, world);
	}
}