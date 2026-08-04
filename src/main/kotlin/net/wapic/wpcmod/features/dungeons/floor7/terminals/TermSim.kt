package net.wapic.wpcmod.features.dungeons.floor7.terminals

import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.DyeColor
import net.wapic.wpcmod.util.MC

object TermSim {

	fun open(type: TerminalType) {
		val menuType = when (type) {
			TerminalType.NUMBERS -> MenuType.GENERIC_9x4
			TerminalType.PANES, TerminalType.RUBIX, TerminalType.STARTS_WITH -> MenuType.GENERIC_9x5
			TerminalType.SELECT_ALL, TerminalType.MELODY -> MenuType.GENERIC_9x6
		}

		val title = when (type) {
			TerminalType.SELECT_ALL -> Component.literal(
				"${type.windowName} ${
					DyeColor.entries.random().name.replace(
						"_",
						" "
					).uppercase()
				} items!"
			)
			TerminalType.STARTS_WITH -> Component.literal("${type.windowName} '${"ABCDGIMNRSTW".random()}'?")
			else -> Component.literal(type.windowName)
		}

		MenuScreens.create(menuType, MC.instance, Int.MAX_VALUE, title)
	}
}