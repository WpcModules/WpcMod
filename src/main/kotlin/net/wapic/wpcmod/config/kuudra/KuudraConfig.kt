package net.wapic.wpcmod.config.kuudra

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.Category
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class KuudraConfig {

	@Expose
	@ConfigOption(name = "Auto GFS", desc = "Automatically retrieve ender pearls from sack using /gfs")
	@ConfigEditorBoolean
	var autoGfs: Boolean = false

	@Expose
	@Category(name = "ESP", desc = "")
	var esp: ESPConfig = ESPConfig()
}