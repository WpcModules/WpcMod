package net.wapic.wpcmod.config.foraging

import io.github.notenoughupdates.moulconfig.annotations.Category

class ForagingConfig {

	@Category(name = "ESP", desc = "Configure ESP features for Foraging")
	var esp: ESPConfig = ESPConfig()
}