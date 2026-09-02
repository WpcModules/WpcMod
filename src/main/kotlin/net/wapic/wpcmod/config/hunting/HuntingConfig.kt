package net.wapic.wpcmod.config.hunting

import io.github.notenoughupdates.moulconfig.annotations.Category
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class HuntingConfig {
	@ConfigOption(name = "Auto Reel Lasso", desc = "Automatically reel in the lasso when \"REEL\" appears above entity")
	@ConfigEditorBoolean
	var autoReel: Boolean = false

	@Category(name = "Safari", desc = "Configurations for Safari")
	var safari: SafariConfig = SafariConfig()
}