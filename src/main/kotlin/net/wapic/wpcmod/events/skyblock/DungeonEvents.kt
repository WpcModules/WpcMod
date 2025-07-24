package net.wapic.wpcmod.events.skyblock

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

object DungeonEvents {

	@JvmField
	val START: Event<Start> = EventFactory.createArrayBacked(Start::class.java) { listeners ->
		Start { ->
			for (listener in listeners) {
				listener.onStart()
			}
		}
	}

	fun interface Start {
		fun onStart()
	}

}