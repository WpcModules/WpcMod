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
}