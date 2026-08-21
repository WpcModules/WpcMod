package net.wapic.wpcmod.config.foraging

import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDraggableList
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import net.wapic.wpcmod.config.components.NonGlowableESPConfig
import net.wapic.wpcmod.features.foraging.ForestNodeESP

class ESPConfig {

	@Accordion
	@ConfigOption(name = "Forest Node", desc = "Forest Node Settings")
	var forestNode = ForestNodeConfig()

	class ForestNodeConfig : NonGlowableESPConfig() {

		@ConfigEditorDraggableList
		@ConfigOption(name = "Enabled Islands", desc = "Enable Forest Node ESP on specific islands")
		var enabledIslands: List<ForestNodeESP.ForestNodeIslands> = mutableListOf(
			ForestNodeESP.ForestNodeIslands.TORRHUS_CANYON,
			ForestNodeESP.ForestNodeIslands.GALATEA,
			ForestNodeESP.ForestNodeIslands.SAFARI
		)

		@ConfigOption(
			name = "Only show current Safari Biome",
			desc = "When in Safari, only show Forest Nodes in the current biome"
		)
		@ConfigEditorBoolean
		var onlyCurrentBiome: Boolean = false
	}
}