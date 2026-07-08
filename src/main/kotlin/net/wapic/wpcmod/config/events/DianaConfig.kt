package net.wapic.wpcmod.config.events

import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import net.wapic.wpcmod.config.components.GlowableESPConfig

class DianaConfig {

	@ConfigOption(name = "Auto Answer Sphinx", desc = "Automatically answer sphinx questions")
	@ConfigEditorBoolean
	var autoAnswerSphinx: Boolean = false

	@ConfigOption(name = "Lootshare Helper", desc = "")
	@Accordion
	var lootshareHelper: LootshareHelperConfig = LootshareHelperConfig()

	class LootshareHelperConfig : GlowableESPConfig() {

		@ConfigOption(name = "Title on Hit", desc = "Send a title when the entity is hit")
		@ConfigEditorBoolean
		var titleOnHit: Boolean = false

		@ConfigOption(name = "Sound on Hit", desc = "Play a sound when the entity is hit")
		@ConfigEditorBoolean
		var soundOnHit: Boolean = false

		@ConfigOption(name = "Enable Lootshare Helper", desc = "Enable Lootshare Helper features")
		@ConfigEditorBoolean
		var enabled: Boolean = false
	}
}