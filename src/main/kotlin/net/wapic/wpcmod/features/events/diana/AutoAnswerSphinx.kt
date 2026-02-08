package net.wapic.wpcmod.features.events.diana

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils

object AutoAnswerSphinx {

	private val config = WpcMod.config.events.diana

	private val questionsToAnswers = mapOf(
		"Who owns the Gold Essence Shop?" to "Marigold",
		"Who helps you apply Rod Parts?" to "Roddy",
		"How many floors are there in The Catacombs?" to "7",
		"How do you obtain the Dark Purple Dye?" to "Dark Auction",
		"Which of these is NOT a pet?" to "Slime",
		"Which of these is NOT a type of Gemstone?" to "Prismite",
		"Who runs the Chocolate Factory?" to "Hoppity",
		"What type of mob is exclusive to the Fishing Festival?" to "Shark",
		"Where is the Titanoboa found?" to "Backwater Bayou",
		"Which type of Gemstone has the lowest Breaking Power?" to "Ruby",
		"What item do you use to kill Pests?" to "Vacuum",
		"What does Junker Joel collect?" to "Junk",
		"Where is Trevor the Trapper found?" to "Mushroom Desert",
		"Which item rarity comes after Mythic?" to "Divine",
		"What is the first type of slayer Maddox offers?" to "Zombie",
	)
	private var nextAnswer: String? = null

	fun init() {
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
		WorldChangeEvent.AFTER.register { nextAnswer = null }
	}

	fun onMessageReceived(message: Component, actionBar: Boolean) {
		if (actionBar || Utils.getLocation() != Island.HUB || !config.autoAnswerSphinx) return
		if (nextAnswer == null) {
			nextAnswer = questionsToAnswers[message.string]
		}

		nextAnswer?.let { answer ->
			WpcMod.logger.info("${message.string} $answer")
			if (message.string.matches(Regex("§7\\s{3}[ABC]\\) §f$answer"))) {
				val clickEvent = message.style.clickEvent as? ClickEvent.RunCommand
				nextAnswer = null
				Utils.runCommand(clickEvent?.command ?: return ChatUtils.sendMessage("Unable to find click event"))
			}
		}
	}
}