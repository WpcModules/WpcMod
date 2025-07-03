package net.wapic.wpcmod.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.wapic.wpcmod.events.PacketEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

	@Inject(at = @At("HEAD"), method = "onParticle")
	private void onParticle(ParticleS2CPacket packet, CallbackInfo info) {
		PacketEvents.PARTICLE.invoker().onParticle(packet);
	}
}