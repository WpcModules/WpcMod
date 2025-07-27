package net.wapic.wpcmod.config.end

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.*

class ESPConfig {

	@Expose
	@Accordion
	@ConfigOption(name = "Dragon ESP", desc = "Dragon ESP Settings")
	var dragon = DragonConfig()

	class DragonConfig() {

		@Expose
		@ConfigOption(name = "Glow ESP", desc = "render a glow around the entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@Expose
		@ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on this entity")
		@ConfigEditorColour
		var color = ChromaColour(1f, 0f, 1f, 0, 0xFF)

		@Expose
		@ConfigOption(name = "Box ESP", desc = "draw an ESP Box around the entity")
		@ConfigEditorBoolean
		var box: Boolean = false

		@Expose
		@ConfigOption(name = "Tracer", desc = "draw a line from your crosshair to the entity")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}

	@Expose
	@Accordion
	@ConfigOption(name = "End Node ESP", desc = "End Node ESP Settings")
	var endNode = EndNodeConfig()

	class EndNodeConfig() {

		@Expose
		@ConfigOption(name = "ESP Search Radius", desc = "sets the radius to search blocks around the player")
		@ConfigEditorSlider(minStep = 1.0f, minValue = 0.0f, maxValue = 50.0f)
		var radius: Float = 25.0f

		@Expose
		@ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on the node")
		@ConfigEditorColour
		var color = ChromaColour(1f, 1f, 1f, 0, 0xFF)

		@Expose
		@ConfigOption(name = "Box ESP", desc = "draw an ESP Box around the node")
		@ConfigEditorBoolean
		var box: Boolean = false

		@Expose
		@ConfigOption(name = "Tracer", desc = "draw a line from your crosshair to the node")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}
}