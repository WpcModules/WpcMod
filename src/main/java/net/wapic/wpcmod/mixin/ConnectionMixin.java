package net.wapic.wpcmod.mixin;

import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.wapic.wpcmod.events.PacketEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin {

	@Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
	private void onPacketSend(Packet<?> packet, ChannelFutureListener channelFutureListener, boolean flush, CallbackInfo ci) {
		PacketEvents.SEND.invoker().onPacketSend(packet, ci);
	}
}
