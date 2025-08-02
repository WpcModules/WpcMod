package net.wapic.wpcmod.config.dungeon

import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorColour
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class ESPConfig {

	@Accordion
	@ConfigOption(name = "Starred Mobs", desc = "Any entity with a star in their name")
	var starMob = StarMobConfig()

	class StarMobConfig() {

		@ConfigOption(name = "Glow", desc = "Render a glow around the entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@ConfigOption(name = "Color", desc = "Color of the glow")
		@ConfigEditorColour
		var color = ChromaColour(1f, 0f, 0f, 0, 0xff)
	}

	@Accordion
	@ConfigOption(name = "Bats", desc = "Any Bat entity even non secret ones")
	var bat = BatConfig()

	class BatConfig() {

		@ConfigOption(name = "Glow", desc = "Render a glow around the entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@ConfigOption(name = "Color", desc = "Color of the glow")
		@ConfigEditorColour
		var color = ChromaColour(1f, 0f, 0f, 0, 0xff)
	}

	@Accordion
	@ConfigOption(name = "Mini Bosses", desc = "Mini Bosses are mobs like Lost Adventurer and Frozen Adventurer")
	var miniboss = MiniBossConfig()

	class MiniBossConfig() {

		@ConfigOption(name = "Glow", desc = "Render a glow around the entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@ConfigOption(name = "Color", desc = "Color of the glow")
		@ConfigEditorColour
		var color = ChromaColour(1f, 0f, 0f, 0, 0xff)
	}
}