package net.wapic.wpcmod.config.chat

import io.github.notenoughupdates.moulconfig.annotations.Category
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorText
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class ChatConfig {

	@ConfigOption(name = "Longer Chat History", desc = "Set the chat history limit higher than you'll ever scroll")
	@ConfigEditorBoolean
	var longerChatHistory: Boolean = false

	@ConfigOption(name = "Compact Chat", desc = "Compact duplicate chat messages")
	@ConfigEditorBoolean
	var compactChat: Boolean = false

	@ConfigOption(name = "Compact Chat Timeout", desc = "Time in seconds until messages won't be counted as duplicate")
	@ConfigEditorSlider(minStep = 1f, minValue = 1f, maxValue = 300f)
	var compactTimeout: Float = 60f

	@Category(name = "Spam Filter", desc = "Configure how to handle chat spam")
	var spam: SpamConfig = SpamConfig()

	@ConfigOption(name = "Auto Party Accept", desc = "Auto accept party invites from people in list (comma separated)")
	@ConfigEditorText
	var autoPartyAccept: String = ""
}