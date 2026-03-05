package net.wapic.wpcmod.config.slayer

import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class SlayerConfig {

	@ConfigOption(name = "Gummy Bear Timer", desc = "Show a countdown for reheated gummy bear")
	@ConfigEditorBoolean
	var gummyBearTimer: Boolean = false
}