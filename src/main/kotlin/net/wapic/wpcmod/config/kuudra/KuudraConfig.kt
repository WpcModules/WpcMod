package net.wapic.wpcmod.config.kuudra

import io.github.notenoughupdates.moulconfig.annotations.Category
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class KuudraConfig {

	@ConfigOption(
		name = "Auto GFS Ender Pearls",
		desc = "Automatically retrieve ender pearls at the start of the run using /gfs"
	)
	@ConfigEditorBoolean
	var autoGfs: Boolean = false

	@ConfigOption(name = "Health Display", desc = "Show the health of kuudra on the screen in P5")
	@ConfigEditorBoolean
	var healthDisplay: Boolean = false

	@ConfigOption(name = "Rend Announce", desc = "Shows rend damage done in chat when someone pulls")
	@ConfigEditorBoolean
	var rendAnnounce: Boolean = false

	@ConfigOption(name = "Cancel Interact", desc = "Allow spamming pearls to the ground")
	@ConfigEditorBoolean
	var cancelInteract: Boolean = false

	@Category(name = "ESP", desc = "Configure ESP features in Kuudra")
	var esp: ESPConfig = ESPConfig()
}