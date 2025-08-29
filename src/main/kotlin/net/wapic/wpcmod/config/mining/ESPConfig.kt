package net.wapic.wpcmod.config.mining

import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.*

class ESPConfig {

	@Accordion
	@ConfigOption(name = "Chest", desc = "Chests")
	var chest = chestConfig()

	class chestConfig() {
		@ConfigOption(name = "Color", desc = "Color for boxes and tracers")
		@ConfigEditorColour
		var color = ChromaColour(1f, 1f, 1f, 0, 0xFF)

		@ConfigOption(name = "Box", desc = "Draw a box around the chest")
		@ConfigEditorBoolean
		var box: Boolean = false

		@ConfigOption(name = "Tracer", desc = "Draw a line from your crosshair to the chest")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}
}