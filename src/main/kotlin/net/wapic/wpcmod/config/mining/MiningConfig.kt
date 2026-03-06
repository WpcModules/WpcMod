package net.wapic.wpcmod.config.mining

import io.github.notenoughupdates.moulconfig.annotations.Category
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class MiningConfig {

	@Category(name = "ESP", desc = "Configure ESP features for mining")
	var esp: ESPConfig = ESPConfig()

	@ConfigOption(name = "Pigeon Swapper", desc = "Auto-swap to pickaxe or drill after using Royal Pigeon")
	@ConfigEditorBoolean
	var pigeonSwapper: Boolean = false

}
