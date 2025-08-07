package net.wapic.wpcmod.config.render

import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean

class RenderConfig {

	@ConfigOption(name = "Fullbright", desc = "Makes things not dark")
	@ConfigEditorBoolean
	var fullbright: Boolean = false

	@ConfigOption(name = "No Blindness", desc = "Makes things not dark when its really dark")
	@ConfigEditorBoolean
	var noBlindness: Boolean = false

	@ConfigOption(name = "Disable Fluid Fog", desc = "Disable fog in fluids")
	@ConfigEditorBoolean
	var disableFluidFog: Boolean = false

	@ConfigOption(name = "No Front Camera", desc = "Disables the front facing camera from f5")
	@ConfigEditorBoolean
	var disableFrontCamera: Boolean = false

}