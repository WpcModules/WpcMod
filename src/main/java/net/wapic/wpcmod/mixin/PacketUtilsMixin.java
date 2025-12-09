package net.wapic.wpcmod.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.wapic.wpcmod.events.PacketEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PacketUtils.class)
public class PacketUtilsMixin {


	@ModifyArg(method = "ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketProcessor;scheduleIfPossible(Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/protocol/Packet;)V"))
	private static <T extends PacketListener> T processPacket(T listener, @Local(argsOnly = true) Packet<T> packet) {
		PacketEvents.RECEIVE.invoker().onPacketReceive(packet);
		return listener;
	}
}
