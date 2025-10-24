package net.wapic.wpcmod.config.dungeon

import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDropdown
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class ScoreCalculationConfig {

	@ConfigOption(name = "Enabled", desc = "Global Toggle for Score Calculation features")
	@ConfigEditorBoolean
	var enabled: Boolean = false

	@ConfigOption(name = "Score Hud", desc = "Show dungeon score in a HUD element")
	@ConfigEditorDropdown
	var scoreHudType: ScoreHudType = ScoreHudType.FULL

	@ConfigOption(name = "Mimic Message", desc = "Alert in chat when Mimic has been killed")
	@ConfigEditorBoolean
	var mimicMessage: Boolean = false

	@ConfigOption(name = "Prince Message", desc = "Alert in chat when Prince has been killed")
	@ConfigEditorBoolean
	var princeMessage: Boolean = false

	@ConfigOption(name = "Score Message 270", desc = "Send score message in party chat")
	@ConfigEditorDropdown
	var scoreMessage270: ScoreMessageType = ScoreMessageType.MESSAGE

	@ConfigOption(name = "Score Message 300", desc = "Send score message only at 300 score")
	@ConfigEditorDropdown
	var scoreMessage300: ScoreMessageType = ScoreMessageType.MESSAGE_AND_TITLE

	@ConfigOption(
		name = "Assume Paul",
		desc = "Assume Paul is active Mayor with +10 bonus score (In case of issues with API)"
	)
	@ConfigEditorBoolean
	var assumePaul: Boolean = false

	@ConfigOption(name = "Assume Spirit Pet", desc = "Assume the first death is with spirit pet")
	@ConfigEditorBoolean
	var assumeSpirit: Boolean = false

	enum class ScoreMessageType(val label: String) {
		DISABLED("Disabled"),
		MESSAGE("Message Only"),
		TITLE("Title Only"),
		MESSAGE_AND_TITLE("Message And Title");

		override fun toString(): String = label
	}

	enum class ScoreHudType(val label: String) {
		DISABLED("Disabled"),
		MINIMIZED("Minimized"),
		FULL("Full");

		override fun toString(): String = label
	}
}
