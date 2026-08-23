package net.wapic.wpcmod.config.hunting

import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import net.wapic.wpcmod.config.components.GlowableESPConfig
import net.wapic.wpcmod.config.components.NonGlowableESPConfig

class SafariConfig {

	@ConfigOption(name = "Announce Sparkling Critters", desc = "Announce when a nearby sparkling critter is spotted")
	@ConfigEditorBoolean
	var announceSparkling: Boolean = false

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
	@ConfigOption(name = "Honeybug Nest ESP", desc = "")
	val honeybugNestESP = HoneybugNestESPConfig()

	class HoneybugNestESPConfig : NonGlowableESPConfig()

	@Accordion
	@ConfigOption(name = "Critter ESP", desc = "")
	val critter = CritterESPConfig()

	class CritterESPConfig : GlowableESPConfig() {

		@ConfigOption(
			name = "Show out of bounds litterbugs",
			desc = "Highlight litterbugs that are at the bottom of the map in Haunted biome"
		)
		@ConfigEditorBoolean
		var showOutOfBoundsLitterbug: Boolean = false
	}
}