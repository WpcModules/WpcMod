package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.network.PacketListener
import net.minecraft.network.protocol.Packet
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
	val SEND_BEFORE: Event<PacketSendBefore> =
		EventFactory.createArrayBacked(PacketSendBefore::class.java) { listeners ->
			PacketSendBefore { packet, callbackInfo ->
			for (listener in listeners) {
				listener.onPacketSendBefore(packet, callbackInfo)
			}
		}
	}

	fun interface PacketSendBefore {
		fun onPacketSendBefore(packet: Packet<out PacketListener>, callbackInfo: CallbackInfo)
	}

	@JvmField
	val SEND_AFTER: Event<PacketSendAfter> = EventFactory.createArrayBacked(PacketSendAfter::class.java) { listeners ->
		PacketSendAfter { packet, callbackInfo ->
			for (listener in listeners) {
				listener.onPacketSendAfter(packet, callbackInfo)
			}
		}
	}

	fun interface PacketSendAfter {
		fun onPacketSendAfter(packet: Packet<out PacketListener>, callbackInfo: CallbackInfo)
	}
}