package net.wapic.wpcmod.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.wapic.wpcmod.events.PacketEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(NetworkThreadUtils.class)
public class NetworkThreadUtilsMixin {


	@ModifyArg(method = "forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/thread/ThreadExecutor;executeSync(Ljava/lang/Runnable;)V"))
	private static <T extends PacketListener> Runnable processPacket(Runnable runnable, @Local(argsOnly = true) Packet<T> packet) {
		PacketEvents.RECEIVE.invoker().onPacketReceive(packet);
		return runnable;
	}
}
