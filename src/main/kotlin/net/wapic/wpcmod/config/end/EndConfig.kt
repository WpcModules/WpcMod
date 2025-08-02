package net.wapic.wpcmod.config.end

import io.github.notenoughupdates.moulconfig.annotations.Category

class EndConfig {

	@Category(name = "ESP", desc = "Configure ESP features for The End")
	var esp: ESPConfig = ESPConfig()
}