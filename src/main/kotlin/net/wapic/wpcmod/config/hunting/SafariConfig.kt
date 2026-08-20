package net.wapic.wpcmod.config.hunting

import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import net.wapic.wpcmod.config.components.GlowableESPConfig

class SafariConfig {

	@ConfigOption(name = "Highlight Honeybug Nests", desc = "Highlights Bee Nests in the forest biome")
	@ConfigEditorBoolean
	var highlightBeehives: Boolean = false

	@Accordion
	@ConfigOption(name = "Critter ESP", desc = "")
	val critter = CritterESPConfig()

	class CritterESPConfig : GlowableESPConfig()
}