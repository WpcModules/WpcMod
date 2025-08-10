package net.wapic.wpcmod.util

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.text.Text
import net.wapic.wpcmod.events.skyblock.DungeonEvents

object DungeonUtils {
	private const val DUNGEON_START_MESSAGE: String =
		"§e[NPC] §bMort§f: Here, I found this map when I first entered the dungeon."

	fun init() {
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
	}

	private fun onMessageReceived(message: Text, actionBar: Boolean) {
		if (actionBar) return

		if (message.string == DUNGEON_START_MESSAGE) {
			DungeonEvents.START.invoker().onStart()
		}
	}
}