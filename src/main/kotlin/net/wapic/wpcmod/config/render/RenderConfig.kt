package net.wapic.wpcmod.config.render

import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class RenderConfig {

	@ConfigOption(name = "Fullbright", desc = "Makes things not dark")
	@ConfigEditorBoolean
	var fullbright: Boolean = false

	@ConfigOption(
		name = "Blindness Opacity",
		desc = "Set the opacity of blindness, 0.0 = no blindness, 1.0 = normal blindness"
	)
	@ConfigEditorSlider(minValue = 0.0f, maxValue = 1.0f, minStep = 0.1f)
	var blindnessOpacity: Float = 1.0f

	@ConfigOption(name = "Disable Fluid Fog", desc = "Disable fog in fluids")
	@ConfigEditorBoolean
	var disableFluidFog: Boolean = false

	@ConfigOption(name = "No Front Camera", desc = "Disables the front facing camera from f5")
	@ConfigEditorBoolean
	var disableFrontCamera: Boolean = false

}