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

	@ConfigOption(name = "Hide in Boss", desc = "Hide HUD element in boss")
	@ConfigEditorBoolean
	var hideInBoss: Boolean = false

	@ConfigOption(name = "Mimic Message", desc = "Alert in chat when Mimic has been killed")
	@ConfigEditorBoolean
	var mimicMessage: Boolean = false

	@ConfigOption(name = "Prince Message", desc = "Alert in chat when Prince has been killed")
	@ConfigEditorBoolean
	var princeMessage: Boolean = false

	@ConfigOption(name = "Notify 270 Score", desc = "Configure 270 score notification")
	@ConfigEditorDropdown
	var scoreMessage270: ScoreMessageType = ScoreMessageType.MESSAGE

	@ConfigOption(name = "Notify 300 Score", desc = "Configure 300 score notification")
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
		SCORE_ONLY("Score Only"),
		MINIMIZED("Minimized"),
		FULL("Full");

		override fun toString(): String = label
	}
}
