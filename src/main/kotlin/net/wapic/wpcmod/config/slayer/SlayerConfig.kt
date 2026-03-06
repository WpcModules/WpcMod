package net.wapic.wpcmod.config.slayer

import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class SlayerConfig {

	@Accordion
	@ConfigOption(name = "Gummy Bear", desc = "Countdown timer for Re-heated Gummy Polar Bear")
	var gummyBearTimer = GummyBearConfig()

	class GummyBearConfig {

		@ConfigOption(name = "Enable", desc = "Display the gummy bear timer")
		@ConfigEditorBoolean
		var enable: Boolean = false

		@ConfigOption(name = "Show Expired", desc = "Show timer display when gummy bear is depleted")
		@ConfigEditorBoolean
		var showExpired: Boolean = false
	}
}