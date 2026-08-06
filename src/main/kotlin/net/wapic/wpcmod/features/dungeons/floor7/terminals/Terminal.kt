package net.wapic.wpcmod.features.dungeons.floor7.terminals

import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.DyeColor
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator.*
import net.wapic.wpcmod.util.MC

object Terminal {

	private val config get() = WpcMod.config.dungeon.floor7.terminalSolvers

	val STARTS_WITH_PATTERN = Regex("^What starts with: '(\\w)'\\?$")
	val SELECT_ALL_PATTERN = Regex("^Select all the (.+) items!$")
	var handler: TerminalSimulatorHandler? = null

	fun createSolverScreen(menu: ChestMenu, title: Component): Screen {
		val terminalType = Type.fromTitle(title)
		return terminalType.screenFactory(menu, title)
	}

	fun createSimulatorHandler(menu: ChestMenu, title: Component) {
		val terminalType = Type.fromTitle(title)
		handler = terminalType.simulatorFactory(menu, title)
	}

	fun shouldReplace(title: Component): Boolean {
		return Type.entries.any { title.string.startsWith(it.windowName) }
	}

	fun isSolverEnabled(): Boolean = config.enabled

	fun openSimulator(type: Type = Type.entries.random()) {
		val title = Component.literal(type.windowName)
		if(type == Type.STARTS_WITH) title.append(" '${"ABCDEFGHIJLMNOW".random()}'?")
		if(type == Type.SELECT_ALL) title.append(" ${DyeColor.entries.random().name.uppercase().replace("_", " ")} items!")

		val menuType = when (type) {
			Type.NUMBERS -> MenuType.GENERIC_9x4
			Type.PANES -> MenuType.GENERIC_9x5
			Type.RUBIX -> MenuType.GENERIC_9x5
			Type.STARTS_WITH -> MenuType.GENERIC_9x5
			Type.SELECT_ALL -> MenuType.GENERIC_9x6
			Type.MELODY -> MenuType.GENERIC_9x6
		}

		MenuScreens.create(menuType, MC.instance, Int.MAX_VALUE, title)
	}

	enum class Type(
		val windowName: String,
		val screenFactory: (menu: ChestMenu, title: Component) -> Screen,
		val simulatorFactory: (menu: ChestMenu, title: Component) -> TerminalSimulatorHandler
	) {
		PANES("Correct all the panes!", ::PanesTerminalScreen, ::PanesSimulatorHandler),
		RUBIX("Change all to same color!", ::RubixTerminalScreen, ::RubixSimulatorHandler),
		NUMBERS("Click in order!", ::NumbersTerminalScreen, ::NumbersSimulatorHandler),
		STARTS_WITH("What starts with:", ::StartsWithTerminalScreen, ::StartsWithSimulatorHandler),
		SELECT_ALL("Select all the", ::SelectAllTerminalScreen, ::SelectAllSimulatorHandler),
		MELODY("Click the button on time!", ::MelodyTerminalScreen, ::MelodySimulatorHandler);

		companion object {
			fun fromTitle(title: Component): Type {
				return entries.first { title.string.startsWith(it.windowName) }
			}
		}
	}
}