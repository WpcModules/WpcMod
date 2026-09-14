package net.wapic.wpcmod.features.dungeons.floor7

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.network.chat.Component
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.MC

object AutoSelectRelic {

	private val config get() = WpcMod.config.dungeon.floor7

	private val relicPickupRegex = Regex("^(.{1,16}) picked the Corrupted (?:Red|Orange|Blue|Green|Purple) Relic!$")
	private val teleportRegex = Regex("^You have teleported to .{1,16}!$")
	private var shouldSelectRelic = false

	fun init() {
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
		WorldChangeEvent.AFTER.register { shouldSelectRelic = false }
	}

	private fun onMessageReceived(message: Component, isActionBar: Boolean) {
		if (isActionBar || !config.autoSelectRelic) return

		if (relicPickupRegex.matchEntire(message.string)?.groups[1]?.value == MC.player?.name?.string) {
			shouldSelectRelic = true
			return
		}

		if (teleportRegex.matches(message.string) && shouldSelectRelic) {
			if (MC.screen == null) {
				MC.player?.inventory?.selectedSlot = 8
			} else {
				ChatUtils.sendMessage("An inventory was open, unable to select relic")
			}
			shouldSelectRelic = false
		}
	}
}