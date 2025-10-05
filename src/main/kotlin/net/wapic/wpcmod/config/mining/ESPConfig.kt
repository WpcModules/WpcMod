package net.wapic.wpcmod.config.mining

import io.github.notenoughupdates.moulconfig.annotations.*
import net.wapic.wpcmod.config.components.NonGlowableESPConfig

class ESPConfig {

	@Accordion
	@ConfigOption(name = "Chest", desc = "Chests")
	var chest: ChestConfig = ChestConfig()

	class ChestConfig() : NonGlowableESPConfig() {
		@ConfigOption(name = "Radius limit", desc = "Sets the limit of blocks around the player to search for chests")
		@ConfigEditorSlider(minStep = 1.0f, minValue = 0.0f, maxValue = 200.0f)
		var radius: Float = 30.0f
	}
}