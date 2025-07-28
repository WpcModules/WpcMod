package net.wapic.wpcmod.listeners

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.text.Text
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.events.skyblock.KuudraEvents

class ChatListener {

	companion object {

		private const val DUNGEON_START_MESSAGE: String =
			"§e[NPC] §bMort§f: Here, I found this map when I first entered the dungeon."
		private const val KUUDRA_START_MESSAGE: String =
			"§e[NPC] §cElle§f: Okay adventurers, I will go and fish up Kuudra!"
		private const val KUUDRA_END_MESSAGE: String =
			"§e[NPC] §cElle§f: POW! SURELY THAT'S IT! I don't think he has any more in him!"
	}

	init {
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
	}

	fun onMessageReceived(text: Text, actionBar: Boolean) {
		if (actionBar) return

		// Dungeons
		if (text.string.equals(DUNGEON_START_MESSAGE)) DungeonEvents.START.invoker().onStart()

		// Kuudra
		if (text.string.equals(KUUDRA_START_MESSAGE)) KuudraEvents.START.invoker().onStart()
		if (text.string.equals(KUUDRA_END_MESSAGE)) KuudraEvents.END.invoker().onEnd()
	}
}