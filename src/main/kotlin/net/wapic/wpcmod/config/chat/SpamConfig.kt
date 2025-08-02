package net.wapic.wpcmod.config.chat

import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDropdown
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class SpamConfig {

	@ConfigOption(name = "Ability Hit", desc = "Your Implosion hit 1 enemy for 128,175.2 damage.")
	@ConfigEditorDropdown
	var abilityHit: SpamType = SpamType.SHOW

	@ConfigOption(name = "Teleport Failed", desc = "There are blocks in the way!")
	@ConfigEditorDropdown
	var tpFail: SpamType = SpamType.SHOW

	@ConfigOption(name = "Player Join / Leave", desc = "Friend > Wapic joined.")
	@ConfigEditorDropdown
	var joinOrLeave: SpamType = SpamType.SHOW

	@ConfigOption(name = "Kill Combo", desc = "&a+5 Kill Combo +3% ✯ Magic Find")
	@ConfigEditorDropdown
	var killCombo: SpamType = SpamType.SHOW

	enum class SpamType(val label: String) {
		SHOW("Show"),
		HIDE("Hide"),
		NOTIFICATION("Notify");

		override fun toString(): String {
			return label
		}
	}
}