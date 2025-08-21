package net.wapic.wpcmod.config.mining;

import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

class MiningConfig {

	@ConfigOption(name = "Pigeon Swapper", desc = "Auto-swap to pigeon")
	@ConfigEditorBoolean
	var pigeonSwapper: Boolean = false
}
