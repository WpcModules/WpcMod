package net.wapic.wpcmod.config.chat

import io.github.notenoughupdates.moulconfig.annotations.*

class ChatConfig {

	@ConfigOption(name = "Longer Chat History", desc = "Set the chat history limit higher than you'll ever scroll")
	@ConfigEditorBoolean
	var longerChatHistory: Boolean = false

	@ConfigOption(name = "Remove Blank Messages", desc = "Removes blank messages from chat")
	@ConfigEditorBoolean
	var removeBlank: Boolean = false

	@ConfigOption(name = "Compact Chat", desc = "Compact duplicate chat messages")
	@ConfigEditorBoolean
	var compactChat: Boolean = false

	@ConfigOption(name = "Compact Chat Timeout", desc = "Time in seconds until messages won't be counted as duplicate")
	@ConfigEditorSlider(minStep = 1f, minValue = 1f, maxValue = 300f)
	var compactTimeout: Float = 60f

	@ConfigOption(name = "Chat Emotes", desc = "Replace emotes to be used in chat (i.e :skull:, :cute::, o/, o7)")
	@ConfigEditorBoolean
	var chatEmotes: Boolean = false

	@ConfigOption(name = "Auto Party Accept", desc = "Auto accept party invites from people in list (comma separated)")
	@ConfigEditorText
	var autoPartyAccept: String = ""

	@ConfigOption(name = "Auto Trapper Accept", desc = "Auto accept trapper task")
	@ConfigEditorBoolean
	var autoTrapperAccept: Boolean = false

	@Category(name = "Spam Filter", desc = "Configure how to handle chat spam")
	var spam: SpamConfig = SpamConfig()
}