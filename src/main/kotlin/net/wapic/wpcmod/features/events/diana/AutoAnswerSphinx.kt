package net.wapic.wpcmod.features.events.diana

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils

object AutoAnswerSphinx {
	// I've typed these from memory which will be incorrect almost every time
	// questions shouldn't matter but I'll keep em here for now
	val questionsToAnswers = mapOf(
		"What mob is unique to the Fishing Festival?" to "Shark",
		"Where can the Titanoboa be found?" to "Backwater Bayou",
		"Where is the Purple Dye sold?" to "Dark Auction",
		"Who sells gold essence?" to "Marigold",
		"Which of these is not a pet?" to "Slime",
		"How many floors are there in the Catacombs?" to "7",
	)

	fun init() {
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
	}

	fun onMessageReceived(message: Component, actionBar: Boolean) {
		if (actionBar || Utils.getLocation() != Island.HUB) return

		val clickableMessages = message.siblings.filter {
			it.style.clickEvent?.action() == ClickEvent.Action.RUN_COMMAND
		}

		clickableMessages.forEach { component ->
			val hasAnswer = questionsToAnswers.values.find { answer -> answer in component.string }
			if(hasAnswer != null) {
				val answer = component.string.first().code - 98
				Utils.runCommand("sphinxanswer $answer")
			}
		}
	}
}