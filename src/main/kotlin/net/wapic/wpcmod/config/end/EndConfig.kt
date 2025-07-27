package net.wapic.wpcmod.config.end

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.Category

class EndConfig {

	@Expose
	@Category(name = "ESP", desc = "")
	var esp: ESPConfig = ESPConfig()
}