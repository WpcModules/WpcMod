package net.wapic.wpcmod.config.galatea

import io.github.notenoughupdates.moulconfig.annotations.Category

class GalateaConfig {

	@Category(name = "ESP", desc = "Configure ESP features in Galatea")
	var esp: ESPConfig = ESPConfig()
}