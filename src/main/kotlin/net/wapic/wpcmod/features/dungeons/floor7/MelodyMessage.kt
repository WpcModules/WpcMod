package net.wapic.wpcmod.features.dungeons.floor7

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.floor7.terminals.AbstractTerminalScreen
import net.wapic.wpcmod.features.dungeons.floor7.terminals.MelodyTerminalScreen
import net.wapic.wpcmod.features.dungeons.floor7.terminals.Terminal
import net.wapic.wpcmod.util.Utils

object MelodyMessage {

	private val config get() = WpcMod.config.dungeon.floor7

	fun init() {
		DungeonEvents.TERMINAL_UPDATED.register(::onTerminalUpdated)
		DungeonEvents.TERMINAL_SOLVED.register(::onTerminalSolved)
		DungeonEvents.TERMINAL_OPENED.register(::onTerminalOpened)
	}

	fun onTerminalOpened(screen: AbstractTerminalScreen) {
		if (!config.melodyMessage || screen !is MelodyTerminalScreen) return
		Utils.runCommand("pc Melody Terminal!")
	}

	fun onTerminalSolved(type: Terminal.Type) {
		if (!config.melodyMessage || type != Terminal.Type.MELODY) return
		Utils.runCommand("pc Melody Completed!")
		WpcMod.LOGGER.info("Melody Terminal Completed!")
	}

	fun onTerminalUpdated(screen: AbstractTerminalScreen, slotIndex: Int, itemStack: ItemStack) {
		if (!config.melodyMessage || screen !is MelodyTerminalScreen || slotIndex == 16) return
		if (itemStack.item != Items.DYED_TERRACOTTA.lime) return
		val index = -1 + slotIndex / 9
		Utils.runCommand("pc Melody ${index * 25}%")
	}
}