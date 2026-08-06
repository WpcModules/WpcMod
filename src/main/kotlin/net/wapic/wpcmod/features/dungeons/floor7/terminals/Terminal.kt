package net.wapic.wpcmod.features.dungeons.floor7.terminals

import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.wapic.wpcmod.WpcMod

object Terminal {
	private val config get() = WpcMod.config.dungeon.floor7.terminalSolvers

	fun createScreen(menu: ChestMenu, title: Component): Screen {
		val terminalType = Type.fromTitle(title)
		return when (terminalType) {
			Type.PANES -> PanesTerminalScreen(menu, title)
			Type.MELODY -> MelodyTerminalScreen(menu, title)
			Type.RUBIX -> RubixTerminalScreen(menu, title)
			Type.NUMBERS -> NumbersTerminalScreen(menu, title)
			Type.SELECT_ALL -> SelectAllTerminalScreen(menu, title)
			Type.STARTS_WITH -> StartsWithTerminalScreen(menu, title)
		}
	}

	fun shouldReplace(title: Component): Boolean {
		if(config.enabled) return false
		return Type.entries.any { title.string.startsWith(it.windowName) }
	}

	enum class Type(val windowName: String, val windowSize: Int, val width: Int) {
		PANES("Correct all the panes!", 45, 5),
		RUBIX("Change all to same color!", 45, 3),
		NUMBERS("Click in order!", 36, 7),
		STARTS_WITH("What starts with:", 45, 7),
		SELECT_ALL("Select all the", 54, 7),
		MELODY("Click the button on time!", 54, 7);

		companion object {
			fun fromTitle(title: Component): Type {
				return entries.first { title.string.startsWith(it.windowName) }
			}
		}
	}
}