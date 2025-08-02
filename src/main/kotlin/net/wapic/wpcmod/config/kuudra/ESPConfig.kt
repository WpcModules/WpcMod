package net.wapic.wpcmod.config.kuudra

import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorColour
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class ESPConfig {

	@Accordion
	@ConfigOption(name = "Kuudra", desc = "")
	var kuudra = KuudraConfig()

	class KuudraConfig() {

		@ConfigOption(name = "Glow", desc = "Render a glow around the entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@ConfigOption(name = "Color", desc = "Color for glow and tracer")
		@ConfigEditorColour
		var color = ChromaColour(1f, 0f, 1f, 0, 0xFF)

		@ConfigOption(name = "Tracer", desc = "Draw a line from your crosshair to the entity")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}
}