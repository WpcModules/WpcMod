package net.wapic.wpcmod.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.Category
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDropdown
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

	class SpamConfig {

		@Expose
		@ConfigOption(name = "Ability Hit", desc = "§7Your Implosion hit §c1 §7enemy for §c1000 §7damage.§r")
		@ConfigEditorDropdown
		var abilityHit: SpamType = SpamType.SHOW

		@Expose
		@ConfigOption(name = "Teleport Failed", desc = "§cThere are blocks in the way!§r")
		@ConfigEditorDropdown
		var tpFail: SpamType = SpamType.SHOW

		@Expose
		@ConfigOption(name = "Player Join / Leave", desc = "§aFriend > §bWapic §ejoined.§r")
		@ConfigEditorDropdown
		var joinOrLeave: SpamType = SpamType.SHOW

		@Expose
		@ConfigOption(name = "Kill Combo", desc = "§a+5 Kill Combo §b+3% ✯ Magic Find")
		@ConfigEditorDropdown
		var killCombo: SpamType = SpamType.SHOW
	}

	enum class SpamType(val label: String) {
		SHOW("Show"),
		HIDE("Hide"),
		NOTIFICATION("Notify");

		override fun toString(): String {
			return label
		}
	}
}