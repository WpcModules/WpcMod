package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

object ServerTickEvent {

	@JvmField
	val EVENT: Event<ServerTick> = EventFactory.createArrayBacked(ServerTick::class.java) { listeners ->
		ServerTick { tick ->
			for (listener in listeners) {
				listener.onServerTick(tick)
			}
		}
	}

	fun interface ServerTick {

		fun onServerTick(tick: Long)
	}
}