package net.wapic.wpcmod.config.kuudra

import io.github.notenoughupdates.moulconfig.annotations.Category
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class KuudraConfig {

	@ConfigOption(name = "Auto GFS Ender Pearls", desc = "Automatically retrieve ender pearls from sack using /gfs")
	@ConfigEditorBoolean
	var autoGfs: Boolean = false

	@ConfigOption(name = "Health Display", desc = "Show the health of kuudra in the middle of the screen in P5")
	@ConfigEditorBoolean
	var healthDisplay: Boolean = false

	@Category(name = "ESP", desc = "Configure ESP features in Kuudra")
	var esp: ESPConfig = ESPConfig()
}