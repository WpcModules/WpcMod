package net.wapic.wpcmod.features.chat

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.network.chat.Component
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Utils

object AutoAcceptPartyInvite {
	private val config get() = WpcMod.config.chat.autoPartyAccept

	private val invitePattern = Regex(
		"-----------------------------------------------------\n" +
				"(\\[[A-z]+\\+{0,2}]\\s)?(?<name>\\w{3,16}) has invited you to join their party!\n" +
				"You have 60 seconds to accept. Click here to join!\n" +
				"-----------------------------------------------------"
	)

	fun init() {
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
	}

	private fun onMessageReceived(message: Component, actionBar: Boolean) {
		if(actionBar) return

		val inviteMatcher: MatchResult = invitePattern.matchEntire(message.string) ?: return
		val name = inviteMatcher.groups["name"]?.value?.trim() ?: return

		val players = config.split(",").map { it.trim().uppercase() }

		if (name.uppercase() in players) {
			WpcMod.LOGGER.debug("Auto accepting invite from $name")
			Utils.addToCommandQueue("party accept $name")
		}
	}
}