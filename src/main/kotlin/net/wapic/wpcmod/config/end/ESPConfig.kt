package net.wapic.wpcmod.config.end

import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import net.wapic.wpcmod.config.components.GlowableESPConfig
import net.wapic.wpcmod.config.components.NonGlowableESPConfig

class ESPConfig {

	@Accordion
	@ConfigOption(name = "Ender Dragon", desc = "Ender Dragons")
	var dragon = DragonConfig()

	class DragonConfig : GlowableESPConfig()

	@Accordion
	@ConfigOption(name = "End Nodes", desc = "End Nodes")
	var endNode = EndNodeConfig()

	class EndNodeConfig : NonGlowableESPConfig() {

		@ConfigOption(name = "Search Radius", desc = "Sets the radius to search for End Nodes")
		@ConfigEditorSlider(minStep = 1.0f, minValue = 0.0f, maxValue = 50.0f)
		var radius: Float = 25.0f
	}
}