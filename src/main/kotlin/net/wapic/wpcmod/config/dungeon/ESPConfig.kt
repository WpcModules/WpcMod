package net.wapic.wpcmod.config.dungeon

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorColour
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class ESPConfig {

	@Expose
	@Accordion
	@ConfigOption(name = "Starred Mob ESP", desc = "Starred Mob ESP Settings")
	var starMob = StarMobConfig()

	class StarMobConfig() {

		@Expose
		@ConfigOption(name = "Glow ESP", desc = "render a glow around the entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@Expose
		@ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on this entity")
		@ConfigEditorColour
		var color = ChromaColour(1f, 0f, 0f, 0, 0xff)
	}

	@Expose
	@Accordion
	@ConfigOption(name = "Bat ESP", desc = "Bat ESP Settings")
	var bat = BatConfig()

	class BatConfig() {

		@Expose
		@ConfigOption(name = "Glow ESP", desc = "render a glow around the entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@Expose
		@ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on this entity")
		@ConfigEditorColour
		var color = ChromaColour(1f, 0f, 0f, 0, 0xff)
	}

	@Expose
	@Accordion
	@ConfigOption(name = "Miniboss ESP", desc = "Miniboss ESP Settings")
	var miniboss = MiniBossConfig()

	class MiniBossConfig() {

		@Expose
		@ConfigOption(name = "Glow ESP", desc = "render a glow around the entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@Expose
		@ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on this entity")
		@ConfigEditorColour
		var color = ChromaColour(1f, 0f, 0f, 0, 0xff)
	}
}