package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.network.listener.PacketListener
import net.minecraft.network.packet.Packet
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object PacketEvents {

	@JvmField
	val RECEIVE: Event<PacketReceive> = EventFactory.createArrayBacked(PacketReceive::class.java) { listeners ->
		PacketReceive { packet ->
			for (listener in listeners) {
				listener.onPacketReceive(packet)
			}
		}
	}

	fun interface PacketReceive {
		fun onPacketReceive(packet: Packet<*>)
	}

	@JvmField
	val SEND: Event<PacketSend> = EventFactory.createArrayBacked(PacketSend::class.java) { listeners ->
		PacketSend { entity, callbackInfo ->
			for (listener in listeners) {
				listener.onPacketSend(entity, callbackInfo)
			}
		}
	}

	fun interface PacketSend {
		fun onPacketSend(packet: Packet<out PacketListener>, callbackInfo: CallbackInfo)
	}
}