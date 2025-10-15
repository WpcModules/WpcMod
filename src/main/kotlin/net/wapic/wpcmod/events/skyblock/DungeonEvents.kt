package net.wapic.wpcmod.events.skyblock

import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.TerminalHandler
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.gui.screen.Screen

object DungeonEvents {

	@JvmField
	val START: Event<Start> = EventFactory.createArrayBacked(Start::class.java) { listeners ->
		Start {
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
		End {
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
		PuzzleReset {
			for (listener in listeners) {
				listener.onPuzzleReset()
			}
		}
	}

	fun interface PuzzleReset {
		fun onPuzzleReset()
	}

	@JvmField
	val TERMINAL_SOLVED: Event<TerminalSolve> = EventFactory.createArrayBacked(TerminalSolve::class.java) { listeners ->
		TerminalSolve { terminal ->
			for (listener in listeners) {
				listener.onSolve(terminal)
			}
		}
	}

	fun interface TerminalSolve {
		fun onSolve(terminal: TerminalHandler)
	}

	@JvmField
	val TERMINAL_OPENED: Event<TerminalOpen> = EventFactory.createArrayBacked(TerminalOpen::class.java) { listeners ->
		TerminalOpen { terminal ->
			for (listener in listeners) {
				listener.onOpen(terminal)
			}
		}
	}

	fun interface TerminalOpen {
		fun onOpen(terminal: TerminalHandler)
	}

	@JvmField
	val TERMINAL_CLOSED: Event<TerminalClose> = EventFactory.createArrayBacked(TerminalClose::class.java) { listeners ->
		TerminalClose { terminal ->
			for (listener in listeners) {
				listener.onClose(terminal)
			}
		}
	}

	fun interface TerminalClose {
		fun onClose(terminal: TerminalHandler)
	}

	@JvmField
	val TERMINAL_UPDATED: Event<TerminalUpdate> = EventFactory.createArrayBacked(TerminalUpdate::class.java) { listeners ->
		TerminalUpdate { terminal ->
			for (listener in listeners) {
				listener.onOpen(terminal)
			}
		}
	}

	fun interface TerminalUpdate {
		fun onOpen(terminal: TerminalHandler)
	}

	@JvmField
	val TERMINAL_CLICKED: Event<TerminalClick> = EventFactory.createArrayBacked(TerminalClick::class.java) { listeners ->
		TerminalClick { screen, slot, button ->
			for (listener in listeners) {
				listener.onClick(screen, slot, button)
			}
		}
	}

	fun interface TerminalClick {
		fun onClick(screen: Screen, slot: Int, button: Int)
	}
}