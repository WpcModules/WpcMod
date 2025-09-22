package net.wapic.wpcmod.util

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.text.Text
import net.wapic.wpcmod.events.skyblock.DungeonEvents

object DungeonUtils {
	private const val DUNGEON_START_MESSAGE: String =
		"§e[NPC] §bMort§f: Here, I found this map when I first entered the dungeon."

	var currentFloor: DungeonFloor = DungeonFloor.NONE
		private set

	fun init() {
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
	}

	private fun onMessageReceived(message: Text, actionBar: Boolean) {
		if (actionBar) return

		if (message.string == DUNGEON_START_MESSAGE) {
			DungeonEvents.START.invoker().onStart()
		}
	}

	enum class DungeonFloor(val shortName: String) {
		ENTRANCE("E"),
		FLOOR_1("F1"),
		FLOOR_2("F2"),
		FLOOR_3("F3"),
		FLOOR_4("F4"),
		FLOOR_5("F5"),
		FLOOR_6("F6"),
		FLOOR_7("F7"),
		MASTER_MODE_FLOOR_1("M1"),
		MASTER_MODE_FLOOR_2("M2"),
		MASTER_MODE_FLOOR_3("M3"),
		MASTER_MODE_FLOOR_4("M4"),
		MASTER_MODE_FLOOR_5("M5"),
		MASTER_MODE_FLOOR_6("M6"),
		MASTER_MODE_FLOOR_7("M7"),
		NONE("")
	}
}