package net.wapic.wpcmod.config.components

import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

abstract class GlowableESPConfig : NonGlowableESPConfig() {
	@ConfigOption(name = "Glow", desc = "Render a glow around the entity")
	@ConfigEditorBoolean
	var glow: Boolean = false
}