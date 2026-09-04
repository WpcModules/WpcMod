package net.wapic.wpcmod.config.components

import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorColour
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

abstract class NonGlowableESPConfig : EspConfig {

	@ConfigOption(name = "Color", desc = "The color to use for glow, box, and tracer.")
	@ConfigEditorColour
	override var color = ChromaColour(1f, 1f, 1f, 0, 255)

	@ConfigOption(name = "Box", desc = "Draw a box around the object")
	@ConfigEditorBoolean
	override var box: Boolean = false

	@ConfigOption(name = "Tracer", desc = "Draw a line from your crosshair to the object")
	@ConfigEditorBoolean
	override var tracer: Boolean = false

	@ConfigOption(name = "Tracer Width", desc = "The width of the tracer drawn")
	@ConfigEditorSlider(maxValue = 5f, minStep = 0.1f, minValue = 1f)
	override var tracerWidth = 2f
}