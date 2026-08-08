package net.wapic.wpcmod.events.skyblock

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.world.item.ItemStack
import net.wapic.wpcmod.features.dungeons.floor7.terminals.AbstractTerminalScreen

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
		TerminalSolve { screen, isSimulator ->
			for (listener in listeners) {
				listener.onSolve(screen, isSimulator)
			}
		}
	}

	fun interface TerminalSolve {
		fun onSolve(screen: AbstractTerminalScreen, isSimulator: Boolean)
	}

	@JvmField
	val TERMINAL_OPENED: Event<TerminalOpen> = EventFactory.createArrayBacked(TerminalOpen::class.java) { listeners ->
		TerminalOpen { screen, isSimulator ->
			for (listener in listeners) {
				listener.onOpen(screen, isSimulator)
			}
		}
	}

	fun interface TerminalOpen {
		fun onOpen(screen: AbstractTerminalScreen, isSimulator: Boolean)
	}

	@JvmField
	val TERMINAL_CLOSED: Event<TerminalClose> = EventFactory.createArrayBacked(TerminalClose::class.java) { listeners ->
		TerminalClose {
			for (listener in listeners) {
				listener.onClose()
			}
		}
	}

	fun interface TerminalClose {
		fun onClose()
	}

	@JvmField
	val TERMINAL_UPDATED: Event<TerminalUpdate> = EventFactory.createArrayBacked(TerminalUpdate::class.java) { listeners ->
		TerminalUpdate { screen, items, isSimulator ->
			for (listener in listeners) {
				listener.onUpdate(screen, items, isSimulator)
			}
		}
	}

	fun interface TerminalUpdate {
		fun onUpdate(screen: AbstractTerminalScreen, items: Array<ItemStack?>, isSimulator: Boolean)
	}

	@JvmField
	val TERMINAL_CLICKED: Event<TerminalClick> = EventFactory.createArrayBacked(TerminalClick::class.java) { listeners ->
		TerminalClick { screen, slot, button, isSimulator ->
			for (listener in listeners) {
				listener.onClick(screen, slot, button, isSimulator)
			}
		}
	}

	fun interface TerminalClick {
		fun onClick(screen: AbstractTerminalScreen, slot: Int, button: Int, isSimulator: Boolean)
	}

	@JvmField
	val ROOM_ENTERED: Event<RoomEntered> = EventFactory.createArrayBacked(RoomEntered::class.java) { listeners ->
		RoomEntered { oldRoom, newRoom ->
			for (listener in listeners) {
				listener.onRoomEntered(oldRoom, newRoom)
			}
		}
	}

	fun interface RoomEntered {
		fun onRoomEntered(oldRoom: String, newRoom: String)
	}
}