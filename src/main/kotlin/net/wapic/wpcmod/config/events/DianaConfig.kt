package net.wapic.wpcmod.config.events

import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class DianaConfig {

	@ConfigOption(name = "Auto Answer Sphinx", desc = "Automatically answer sphinx questions")
	@ConfigEditorBoolean
	var autoAnswerSphinx: Boolean = false
}