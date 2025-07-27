package net.wapic.wpcmod.config.galatea

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.Category

class GalateaConfig {

	@Expose
	@Category(name = "ESP", desc = "")
	var esp: ESPConfig = ESPConfig()
}