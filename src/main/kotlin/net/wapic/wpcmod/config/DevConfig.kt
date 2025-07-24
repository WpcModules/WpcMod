package net.wapic.wpcmod.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class DevConfig {

	@Expose
	@ConfigOption(name = "Show SkyBlock ID", desc = "Shows SkyBlock ID in lore")
	@ConfigEditorBoolean
	var showSkyBlockID: Boolean = false
}
