package net.wapic.wpcmod.features.dungeons.floor7.terminals

import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.wapic.wpcmod.WpcMod

enum class TerminalType(val windowName: String, val windowSize: Int, val width: Int) {
	PANES("Correct all the panes!", 45, 5),
	RUBIX("Change all to same color!", 45, 3),
	NUMBERS("Click in order!", 36, 7),
	STARTS_WITH("What starts with:", 45, 7),
	SELECT_ALL("Select all the", 54, 7),
	MELODY("Click the button on time!", 54, 7);

	companion object {
		private val config get() = WpcMod.config.dungeon.floor7.terminalSolvers

		fun fromTitle(title: Component): TerminalType? {
			if (!config.enabled) return null
			return TerminalType.entries.find { title.string.startsWith(it.windowName) }
		}

		fun getScreen(terminalType: TerminalType, menu: ChestMenu, title: Component): Screen {
			return when (terminalType) {
				PANES -> PanesTerminalScreen(menu, title)
				MELODY -> MelodyTerminalScreen(menu, title)
				RUBIX -> RubixTerminalScreen(menu, title)
				NUMBERS -> NumbersTerminalScreen(menu, title)
				SELECT_ALL -> SelectAllTerminalScreen(menu, title)
				STARTS_WITH -> StartsWithTerminalScreen(menu, title)
			}
		}
	}
}