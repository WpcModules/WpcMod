package net.wapic.wpcmod.config.fishing

import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class FishingConfig {

	@Accordion
	@ConfigOption(name = "", desc = "")
	val autofish: AutoFishConfig = AutoFishConfig()

	class AutoFishConfig {

		@ConfigEditorBoolean
		@ConfigOption(name = "Auto Fish", desc = "")
		var enabled: Boolean = false

		@ConfigEditorBoolean
		@ConfigOption(name = "Recast", desc = "Allows you to stop recasting rod after reeling in")
		var recast: Boolean = true

		@ConfigEditorSlider(minStep = 1f, minValue = 200f, maxValue = 350f)
		@ConfigOption(name = "Minimum Delay", desc = "The minimum amount of delay when casting rod")
		var minDelay: Float = 250f

		@ConfigEditorBoolean
		@ConfigOption(name = "Randomize delay", desc = "Randomize delay based on minimum delay")
		var slugFish: Boolean = true
	}
}