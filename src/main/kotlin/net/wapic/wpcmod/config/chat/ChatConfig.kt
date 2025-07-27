package net.wapic.wpcmod.config.chat

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.Category
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class ChatConfig {

	@Expose
	@ConfigOption(name = "Chat History Length", desc = "Set maximum lines in chat history")
	@ConfigEditorSlider(minStep = 1f, minValue = 100f, maxValue = 10000f)
	var chatHistoryLength: Float = 100f

	@Expose
	@Category(name = "Spam Filter Settings", desc = "")
	var spamConfig: SpamConfig = SpamConfig()
}