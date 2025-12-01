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

	class DragonConfig(): GlowableESPConfig()

	@Accordion
	@ConfigOption(name = "End Nodes", desc = "End Nodes")
	var endNode = EndNodeConfig()

	class EndNodeConfig(): NonGlowableESPConfig() {

		@ConfigOption(name = "Search Radius", desc = "Sets the radius to search for End Nodes")
		@ConfigEditorSlider(minStep = 1.0f, minValue = 0.0f, maxValue = 100.0f)
		var radius: Float = 25.0f

		@ConfigOption(name = "Blocks per tick", desc = "Sets the amount of blocks to step through per tick")
		@ConfigEditorSlider(minStep = 50.0f, minValue = 50.0f, maxValue = 50000.0f)
		var blocksPerTick: Float = 1000.0f
	}
}