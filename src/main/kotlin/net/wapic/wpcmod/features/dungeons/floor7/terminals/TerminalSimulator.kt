package net.wapic.wpcmod.features.dungeons.floor7.terminals

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator.AbstractTerminalSimulator
import net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator.MelodyTerminalSimulator
import net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator.NumbersTerminalSimulator
import net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator.PanesTerminalSimulator
import net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator.RubixTerminalSimulator
import net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator.SelectAllTerminalSimulator
import net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator.StartsWithTerminalSimulator
import net.wapic.wpcmod.util.MC

object TerminalSimulator {

	private var sim: AbstractTerminalSimulator? = null
	private var ticks = 0

	fun init() {
		DungeonEvents.TERMINAL_OPENED.register(::onTerminalOpen)
		DungeonEvents.TERMINAL_CLICKED.register(::onTerminalClicked)
		DungeonEvents.TERMINAL_UPDATED.register(::onTerminalUpdated)
		DungeonEvents.TERMINAL_SOLVED.register(::onTerminalSolved)
		DungeonEvents.TERMINAL_CLOSED.register(::onTerminalClosed)
		ClientTickEvents.END_CLIENT_TICK.register(::doTerminalTick)
	}

	fun open(type: Terminal.Type, dyeColor: DyeColor? = null, letter: Char? = null) {
		val menuType = when (type) {
			Terminal.Type.NUMBERS -> MenuType.GENERIC_9x4
			Terminal.Type.PANES, Terminal.Type.RUBIX, Terminal.Type.STARTS_WITH -> MenuType.GENERIC_9x5
			Terminal.Type.SELECT_ALL, Terminal.Type.MELODY -> MenuType.GENERIC_9x6
		}

		val title = when (type) {
			Terminal.Type.SELECT_ALL -> {
				val dyeColor = dyeColor ?: DyeColor.entries.random().name.replace("_", " ").uppercase()
				Component.literal("${type.windowName} $dyeColor items!")
			}
			Terminal.Type.STARTS_WITH -> {
				val letter = letter ?: "ABCDGIMNRSTW".random()
				Component.literal("${type.windowName} '$letter'?")
			}
			else -> Component.literal(type.windowName)
		}

		MenuScreens.create(menuType, MC.instance, Int.MAX_VALUE, title)
	}

	fun onTerminalOpen(screen: AbstractTerminalScreen, isSimulator: Boolean) {
		if(!isSimulator) return

		sim = when(screen) {
			is MelodyTerminalScreen -> MelodyTerminalSimulator(screen)
			is NumbersTerminalScreen -> NumbersTerminalSimulator(screen)
			is PanesTerminalScreen -> PanesTerminalSimulator(screen)
			is RubixTerminalScreen -> RubixTerminalSimulator(screen)
			is SelectAllTerminalScreen -> SelectAllTerminalSimulator(screen)
			is StartsWithTerminalScreen -> StartsWithTerminalSimulator(screen)
			else -> null
		}

		sim?.create()
	}

	fun onTerminalClicked(screen: AbstractTerminalScreen, slotIndex: Int, button: Int, isSimulator: Boolean) {
		sim?.onClick(slotIndex, button)
	}

	fun onTerminalUpdated(screen: AbstractTerminalScreen, items: Array<ItemStack?>, isSimulator: Boolean) {
		sim?.onUpdate(items)
	}

	fun onTerminalSolved(screen: AbstractTerminalScreen, isSimulator: Boolean) {
		sim?.close()
	}

	fun onTerminalClosed(type: Terminal.Type, isSimulator: Boolean) {
		sim = null
	}

	fun doTerminalTick(client: Minecraft) {
		ticks++
		if(ticks % 16 == 0) (sim as? MelodyTerminalSimulator)?.tick()
	}
}