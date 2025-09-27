package net.wapic.wpcmod.config.general

import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorColour
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class ESPConfig {

	@Accordion
	@ConfigOption(name = "Rat", desc = "Rat Settings")
	var rat = RatConfig()

	class RatConfig() {

		@ConfigOption(name = "Glow", desc = "Render a glow around the entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@ConfigOption(name = "Color", desc = "The color to use for glow, box, and tracer.")
		@ConfigEditorColour
		var color = ChromaColour(0f, 1f, 1f, 0, 0xFF)

		@ConfigOption(name = "Box", desc = "Draw a box around the entity")
		@ConfigEditorBoolean
		var box: Boolean = false

		@ConfigOption(name = "Tracer", desc = "Draw a line from your crosshair to the entity")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}

	@Accordion
	@ConfigOption(name = "Entity Tagging", desc = "Options for tagging entities with /wpc tag")
	var tag = Tag()

	class Tag {

		@ConfigOption(name = "Glow", desc = "Render glow around the tagged entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@ConfigOption(
			name = "Tag Outline Color",
			desc = "Color to use when highlighting players with /wpcmod tag command"
		)
		@ConfigEditorColour
		var color = ChromaColour(1f, 1f, 1f, 0, 0xFF)

		@ConfigOption(name = "Glow", desc = "Render a box around the tagged entity")
		@ConfigEditorBoolean
		var box: Boolean = false

		@ConfigOption(name = "Tag Tracer", desc = "draw a tracer line to tagged players/entities")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}
}