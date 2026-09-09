package net.wapic.wpcmod.config.dungeon

import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.*

class PuzzlesConfig {

	@Accordion
	@ConfigOption(name = "Blaze Solver", desc = "")
	var blazeSolver = BlazeConfig()

	class BlazeConfig {

		@ConfigOption(name = "Enable Blaze Solver", desc = "Global toggle for the blaze solver")
		@ConfigEditorBoolean
		var enabled = false

		@ConfigOption(name = "Use Filled Box", desc = "Use a filled box instead of a wireframe")
		@ConfigEditorBoolean
		var filled = false

		@ConfigOption(name = "Blazes to Show", desc = "Amount of blazes to highlight")
		@ConfigEditorSlider(maxValue = 10f, minValue = 1f, minStep = 1f)
		var blazesToShow = 3f

		@ConfigOption(name = "Lines to Show", desc = "Amount of lines to draw")
		@ConfigEditorSlider(maxValue = 10f, minValue = 0f, minStep = 1f)
		var linesToShow = 3f

		@ConfigOption(name = "Line Width", desc = "Width of lines between blazes")
		@ConfigEditorSlider(maxValue = 5f, minValue = 0f, minStep = 1f)
		var lineWidth = 2f

		@ConfigOption(name = "First Blaze Color", desc = "Color of the first blaze to kill")
		@ConfigEditorColour
		var blazeColor0 = ChromaColour.fromStaticRGB(0, 255, 0, 255)

		@ConfigOption(name = "Second Blaze Color", desc = "color of the next blaze to kill")
		@ConfigEditorColour
		var blazeColor1 = ChromaColour.fromStaticRGB(255, 50, 0, 255)

		@ConfigOption(name = "Other Blaze Color", desc = "color of the second next blaze to kill")
		@ConfigEditorColour
		var blazeColor2 = ChromaColour.fromStaticRGB(255, 0, 0, 255)
	}
}