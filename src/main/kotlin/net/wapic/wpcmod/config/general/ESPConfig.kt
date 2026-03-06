package net.wapic.wpcmod.config.general

import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import net.wapic.wpcmod.config.components.GlowableESPConfig

class ESPConfig {

	@Accordion
	@ConfigOption(name = "Rat", desc = "Rat Settings")
	var rat = RatConfig()

	class RatConfig : GlowableESPConfig()

	@Accordion
	@ConfigOption(name = "Entity Tagging", desc = "Options for tagging entities with /wpc tag")
	var tag = Tag()

	class Tag : GlowableESPConfig()
}