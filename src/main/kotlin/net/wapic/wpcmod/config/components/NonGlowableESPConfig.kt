package net.wapic.wpcmod.config.components

import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorColour
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

abstract class NonGlowableESPConfig {

	@ConfigOption(name = "Color", desc = "The color to use for glow, box, and tracer.")
	@ConfigEditorColour
	var color = ChromaColour(1f, 1f, 1f, 0, 0)

	@ConfigOption(name = "Box", desc = "Draw a box around the object")
	@ConfigEditorBoolean
	var box: Boolean = false

	@ConfigOption(name = "Tracer", desc = "Draw a line from your crosshair to the object")
	@ConfigEditorBoolean
	var tracer: Boolean = false
}