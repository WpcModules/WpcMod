package net.wapic.wpcmod.config.end

import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.*

class ESPConfig {

	@Accordion
	@ConfigOption(name = "Ender Dragon", desc = "Ender Dragons")
	var dragon = DragonConfig()

	class DragonConfig() {

		@ConfigOption(name = "Glow", desc = "Render a glow around the entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@ConfigOption(name = "Color", desc = "Color for boxes and tracers")
		@ConfigEditorColour
		var color = ChromaColour(1f, 0f, 1f, 0, 0xFF)

		@ConfigOption(name = "Box", desc = "Draw a box around the entity")
		@ConfigEditorBoolean
		var box: Boolean = false

		@ConfigOption(name = "Tracer", desc = "Draw a line from your crosshair to the entity")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}

	@Accordion
	@ConfigOption(name = "End Nodes", desc = "End Nodes")
	var endNode = EndNodeConfig()

	class EndNodeConfig() {

		@ConfigOption(name = "Search Radius", desc = "Sets the radius to search for End Nodes")
		@ConfigEditorSlider(minStep = 1.0f, minValue = 0.0f, maxValue = 50.0f)
		var radius: Float = 25.0f

		@ConfigOption(name = "Color", desc = "Color for boxes and tracers")
		@ConfigEditorColour
		var color = ChromaColour(1f, 1f, 1f, 0, 0xFF)

		@ConfigOption(name = "Box", desc = "Draw a box around the node")
		@ConfigEditorBoolean
		var box: Boolean = false

		@ConfigOption(name = "Tracer", desc = "Draw a line from your crosshair to the End Node")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}
}