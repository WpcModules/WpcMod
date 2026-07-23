package net.wapic.wpcmod.config.chat

import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDropdown
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorInfoText
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class SpamConfig {
	@ConfigOption(name = "Spam Filter", desc = "Filter out common and annoying messages in chat")
	@ConfigEditorInfoText(infoTitle = "Info")
	private val spamInfoText: Any? = null

	@ConfigOption(name = "Ability Hit", desc = "Your Implosion hit 1 enemy for 128,175.2 damage.")
	@ConfigEditorDropdown
	var abilityHit: SpamType = SpamType.SHOW

	@ConfigOption(name = "Teleport Failed", desc = "There are blocks in the way!")
	@ConfigEditorDropdown
	var tpFail: SpamType = SpamType.SHOW

	@ConfigOption(name = "Player Join / Leave", desc = "Friend > Wapic joined.")
	@ConfigEditorDropdown
	var joinOrLeave: SpamType = SpamType.SHOW

	@ConfigOption(name = "Kill Combo", desc = "+5 Kill Combo +3% ✯ Magic Find")
	@ConfigEditorDropdown
	var killCombo: SpamType = SpamType.SHOW

	@ConfigOption(name = "Tip Messages", desc = "You tipped 2 players in 2 different games!")
	@ConfigEditorDropdown
	var tippedPlayers: SpamType = SpamType.SHOW

	enum class SpamType(val label: String) {
		SHOW("Show"),
		HIDE("Hide"),
		NOTIFICATION("Notify");

		override fun toString(): String = label
	}
}