package net.wapic.wpcmod.config.mining

import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import net.wapic.wpcmod.config.components.NonGlowableESPConfig

class ESPConfig {

	@Accordion
	@ConfigOption(name = "Crystal Hollows Chests", desc = "Chests")
	var chest: ChestConfig = ChestConfig()

	class ChestConfig : NonGlowableESPConfig() {
		@ConfigOption(name = "Radius limit", desc = "Sets the limit of blocks around the player to search for chests")
		@ConfigEditorSlider(minStep = 1.0f, minValue = 0.0f, maxValue = 200.0f)
		var radius: Float = 30.0f
	}

	@Accordion
	@ConfigOption(name = "Mineshaft Corpses", desc = "")
	var corpse: CorpseConfig = CorpseConfig()

	class CorpseConfig : NonGlowableESPConfig() {
		@ConfigOption(name = "Use Corpse Colour", desc = "Use the corpse type for color instead of the color option")
		@ConfigEditorBoolean
		var corpseColor: Boolean = false
	}
}