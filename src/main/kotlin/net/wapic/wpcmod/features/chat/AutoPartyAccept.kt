package net.wapic.wpcmod.features.chat

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.text.Text
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Utils
import java.util.regex.Matcher
import java.util.regex.Pattern

object AutoPartyAccept {
	private val config get() = WpcMod.config.chat.autoPartyAccept

	private val invitePattern: Pattern = Pattern.compile(
		"-----------------------------------------------------\n" +
				"(\\[\\w+\\+?\\]\\s)?(?<name>\\w{3,16}) has invited you to join their party!\n" +
				"You have 60 seconds to accept. Click here to join!\n" +
				"-----------------------------------------------------")

	fun init() {
		ClientReceiveMessageEvents.GAME.register(this::onMessageReceived)
	}

	private fun onMessageReceived(message: Text, actionBar: Boolean) {
		val inviteMatcher: Matcher = invitePattern.matcher(message.string)
		if(actionBar) return
		if(inviteMatcher.find()) {
			val players = config.split(',').map { it.trim().uppercase() }
			if(!players.contains(inviteMatcher.group("name").uppercase())) return
			WpcMod.logger.info("Accepting invite from ${inviteMatcher.group("name")}")
			Utils.addToCommandQueue("party accept " + inviteMatcher.group("name").trim())
		}
	}
}