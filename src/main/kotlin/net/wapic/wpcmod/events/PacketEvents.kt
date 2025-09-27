package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.entity.Entity
import net.minecraft.network.packet.Packet

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
		PacketSend { entity ->
			for (listener in listeners) {
				listener.onPacketSend(entity)
			}
		}
	}

	fun interface PacketSend {
		fun onPacketSend(entity: Entity)
	}
}