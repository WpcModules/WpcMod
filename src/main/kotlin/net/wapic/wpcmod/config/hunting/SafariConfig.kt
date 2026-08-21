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
	@ConfigOption(name = "Safari Tracker", desc = "")
	val tracker = SafariTrackerConfig()

	class SafariTrackerConfig {
		@ConfigOption(name = "Enable Safari Tracker", desc = "Show all captured mobs in the safari")
		@ConfigEditorBoolean
		var showTracker: Boolean = false

		@ConfigOption(name = "Show only current Biome", desc = "Show only mobs in the current biome")
		@ConfigEditorBoolean
		var onlyCurrentBiome: Boolean = false
	}

	@Accordion
	@ConfigOption(name = "Critter ESP", desc = "")
	val critter = CritterESPConfig()

	class CritterESPConfig : GlowableESPConfig()
}