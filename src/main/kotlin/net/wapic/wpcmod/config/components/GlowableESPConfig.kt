package net.wapic.wpcmod.config.components

import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import io.github.notenoughupdates.moulconfig.annotations.ConfigOverride

abstract class GlowableESPConfig : NonGlowableESPConfig(), Glowable {

	@ConfigOverride
	@ConfigOption(name = "Glow", desc = "Render a glow around the entity")
	@ConfigEditorBoolean
	override var glow: Boolean = false
}