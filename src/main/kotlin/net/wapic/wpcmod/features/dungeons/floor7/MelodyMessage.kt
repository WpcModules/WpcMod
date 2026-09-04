package net.wapic.wpcmod.features.dungeons.floor7

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.floor7.terminals.AbstractTerminalScreen
import net.wapic.wpcmod.features.dungeons.floor7.terminals.MelodyTerminalScreen
import net.wapic.wpcmod.util.Utils

object MelodyMessage {

	private val config get() = WpcMod.config.dungeon.floor7

	fun init() {
		DungeonEvents.TERMINAL_UPDATED.register(::onTerminalUpdated)
	}

	fun onTerminalUpdated(screen: AbstractTerminalScreen, slotIndex: Int, itemStack: ItemStack) {
		if (!config.melodyMessage || screen !is MelodyTerminalScreen) return
		if (itemStack.item != Items.DYED_TERRACOTTA.lime) return
		val index = -1 + slotIndex / 9
		Utils.runCommand("pc Melody ${index * 25}%")
	}
}