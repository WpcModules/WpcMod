package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.network.packet.s2c.play.TeamS2CPacket

object ScoreboardChangeEvent {

	@JvmField
	val EVENT: Event<ScoreboardChange> = EventFactory.createArrayBacked(ScoreboardChange::class.java) { listeners ->
		ScoreboardChange { entries ->
			for (listener in listeners) {
				listener.onScoreboardChange(entries)
			}
		}
	}

	@JvmField
	val GENERAL_EVENT: Event<ScoreboardEvent> =
		EventFactory.createArrayBacked(ScoreboardEvent::class.java) { listeners ->
			ScoreboardEvent { entries ->
				for (listener in listeners) {
					listener.onScoreboardChange(entries)
				}
			}
		}

	fun interface ScoreboardChange {
		fun onScoreboardChange(line: String)
	}

	fun interface ScoreboardEvent {
		fun onScoreboardChange(packet: TeamS2CPacket)
	}
}