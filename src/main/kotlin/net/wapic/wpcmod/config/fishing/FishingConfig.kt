package net.wapic.wpcmod.config.fishing

import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class FishingConfig {

	@Accordion
	@ConfigOption(name = "Auto Fish", desc = "")
	val autofish: AutoFishConfig = AutoFishConfig()

	class AutoFishConfig {

		@ConfigEditorBoolean
		@ConfigOption(name = "Auto Fish Enabled", desc = "Automatically reel in and recast rod when active")
		var enabled: Boolean = false

		@ConfigEditorBoolean
		@ConfigOption(name = "Recast", desc = "Won't recast after reeling in. useful when rod doesn't 1 tap")
		var recast: Boolean = true

		@ConfigEditorSlider(minStep = 1f, minValue = 100f, maxValue = 250f)
		@ConfigOption(name = "Minimum Delay", desc = "The minimum amount of delay when casting rod")
		var minDelay: Float = 175f

		@ConfigEditorBoolean
		@ConfigOption(name = "Slug Fish", desc = "wait 20s to catch")
		var slugFish: Boolean = false

		@ConfigEditorBoolean
		@ConfigOption(name = "Assume Slug Pet", desc = "Assumes you have a LVL 100 Slug pet for slug fish")
		var slugPet: Boolean = false
	}
}