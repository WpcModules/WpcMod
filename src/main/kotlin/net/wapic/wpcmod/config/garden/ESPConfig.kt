package net.wapic.wpcmod.config.garden

import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import net.wapic.wpcmod.config.components.GlowableESPConfig

class ESPConfig {
	@Accordion
	@ConfigOption(name = "Pest", desc = "Pest Settings")
	var pest = PestConfig()

	class PestConfig : GlowableESPConfig()
}