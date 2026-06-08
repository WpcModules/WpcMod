package net.wapic.wpcmod.config.inventory

import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorColour
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class ExperimentConfig {

	@ConfigOption(name = "Auto Solve Experiments", desc = "Completes Chronomatron and Ultrasequencer automatically")
	@ConfigEditorBoolean
	var autoExperiments: Boolean = false

	@ConfigOption(name = "Auto Close", desc = "Auto close experiment when done")
	@ConfigEditorBoolean
	var autoClose: Boolean = false

	@ConfigOption(name = "Click delay", desc = "Delay between clicks on experiments")
	@ConfigEditorSlider(minStep = 1.0f, minValue = 200.0f, maxValue = 500.0f)
	var clickDelay: Float = 250.0f

	@ConfigOption(name = "Metaphysical Serum", desc = "Sets how many Metaphysical serums have been consumed")
	@ConfigEditorSlider(minStep = 1.0f, minValue = 0.0f, maxValue = 3.0f)
	var serumCount: Float = 0.0f

	@ConfigOption(name = "Superpairs Solver", desc = "Highlight items in Superpairs similar to NEU")
	@ConfigEditorBoolean
	var superpairsSolver: Boolean = false

	@ConfigOption(name = "Superpair Colors", desc = "")
	@Accordion
	var superpairColors = SuperpairColors()

	class SuperpairColors {

		@ConfigOption(name = "Found Pair", desc = "Color of found pairs")
		@ConfigEditorColour
		var foundPair: ChromaColour = ChromaColour.fromStaticRGB(0, 255, 0, 255)

		@ConfigOption(name = "Power Ups", desc = "Color of Power Ups")
		@ConfigEditorColour
		var powerUp: ChromaColour = ChromaColour.fromStaticRGB(255, 0, 255, 255)

		@ConfigOption(name = "Discovered Pairs", desc = "Color of pairs not yet claimed")
		@ConfigEditorColour
		var discoveredPair: ChromaColour = ChromaColour.fromStaticRGB(0, 255, 255, 255)

		@ConfigOption(name = "Undiscovered Pairs", desc = "Color of items with no matching pair")
		@ConfigEditorColour
		var undiscoveredPair: ChromaColour = ChromaColour.fromStaticRGB(255, 0, 0, 255)
	}
}