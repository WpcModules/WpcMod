package net.wapic.wpcmod.config.dev

import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class DevConfig {

	@ConfigOption(name = "Show SkyBlock ID", desc = "Show SkyBlock ID in item lore")
	@ConfigEditorBoolean
	var showSkyBlockID: Boolean = false
}