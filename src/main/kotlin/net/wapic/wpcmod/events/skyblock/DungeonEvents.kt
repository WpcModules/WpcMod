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

	@JvmField
	val END: Event<End> = EventFactory.createArrayBacked(End::class.java) { listeners ->
		End { ->
			for (listener in listeners) {
				listener.onEnd()
			}
		}
	}

	fun interface End {
		fun onEnd()
	}

	@JvmField
	val PUZZLE_RESET: Event<PuzzleReset> = EventFactory.createArrayBacked(PuzzleReset::class.java) { listeners ->
		PuzzleReset { ->
			for (listener in listeners) {
				listener.onPuzzleReset()
			}
		}
	}

	fun interface PuzzleReset {
		fun onPuzzleReset()
	}

}