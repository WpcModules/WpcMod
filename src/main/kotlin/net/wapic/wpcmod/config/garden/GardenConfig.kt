package net.wapic.wpcmod.config.garden

import io.github.notenoughupdates.moulconfig.annotations.Category

class GardenConfig {
	@Category(name = "ESP", desc = "Configure ESP features in Garden")
	var esp: ESPConfig = ESPConfig()
}