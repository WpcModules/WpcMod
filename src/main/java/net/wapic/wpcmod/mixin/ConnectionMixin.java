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
	private void onPacketSend(Packet<?> packet, ChannelFutureListener listener, boolean flush, CallbackInfo ci) {
		PacketEvents.SEND_BEFORE.invoker().onPacketSendBefore(packet, ci);
	}

	@Inject(method = "sendPacket", at = @At("TAIL"), cancellable = true)
	private void onPacketSendAfter(Packet<?> packet, ChannelFutureListener listener, boolean flush, CallbackInfo ci) {
		PacketEvents.SEND_AFTER.invoker().onPacketSendAfter(packet, ci);
	}
}
