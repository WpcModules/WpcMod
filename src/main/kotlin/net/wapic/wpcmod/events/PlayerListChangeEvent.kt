package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket

object PlayerListChangeEvent {

	@JvmField
	val EVENT: Event<PlayerListChange> = EventFactory.createArrayBacked(PlayerListChange::class.java) { listeners ->
		PlayerListChange { entries ->
			for (listener in listeners) {
				listener.onPlayerListChange(entries)
			}
		}
	}

	fun interface PlayerListChange {
		fun onPlayerListChange(entries: List<PlayerListS2CPacket.Entry>)
	}
}