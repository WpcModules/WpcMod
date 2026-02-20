package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

object ScoreboardChangeEvent {

	@JvmField
	val EVENT: Event<ScoreboardChange> = EventFactory.createArrayBacked(ScoreboardChange::class.java) { listeners ->
		ScoreboardChange { line ->
			for (listener in listeners) {
				listener.onScoreboardChange(line)
			}
		}
	}

	fun interface ScoreboardChange {
		fun onScoreboardChange(line: String)
	}
}