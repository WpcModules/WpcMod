package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

object ServerTickEvent {

	@JvmField
	val EVENT: Event<ServerTick> = EventFactory.createArrayBacked(ServerTick::class.java) { listeners ->
		ServerTick {
			for (listener in listeners) {
				listener.onServerTick()
			}
		}
	}

	fun interface ServerTick {
		fun onServerTick()
	}
}