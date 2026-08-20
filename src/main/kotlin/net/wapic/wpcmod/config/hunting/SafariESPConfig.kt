package net.wapic.wpcmod.config.hunting

import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import net.wapic.wpcmod.config.components.GlowableESPConfig

class SafariESPConfig {

	@Accordion
	@ConfigOption(name = "Critter ESP", desc = "")
	val critter = CritterESPConfig()

	class CritterESPConfig : GlowableESPConfig()
}