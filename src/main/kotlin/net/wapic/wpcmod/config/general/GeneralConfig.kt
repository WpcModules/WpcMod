package net.wapic.wpcmod.config.general

import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.*
import net.minecraft.client.MinecraftClient
import net.wapic.wpcmod.features.general.shortcut.ShortcutScreen

class GeneralConfig {

	@ConfigOption(name = "Fullbright", desc = "Makes things not dark")
	@ConfigEditorBoolean
	var fullbright: Boolean = false

	@ConfigOption(name = "No Blindness", desc = "Makes things not dark when its really dark")
	@ConfigEditorBoolean
	var noBlindness: Boolean = false

	@ConfigOption(name = "Disable Fluid Fog", desc = "Disable fog in fluids")
	@ConfigEditorBoolean
	var disableFluidFog: Boolean = false

	@ConfigOption(name = "Armor Swapper", desc = "Instantly swap to Sorrow when Keybind is hit")
	@ConfigEditorBoolean
	var armorSwapper: Boolean = false

	@ConfigOption(name = "Prevent Placing Items", desc = "Prevent placing items such as Weird Tuba and Flower of Truth")
	@ConfigEditorBoolean
	var preventPlacing: Boolean = false

	@ConfigOption(name = "No Front Camera", desc = "Disables the front facing camera from f5")
	@ConfigEditorBoolean
	var disableFrontCamera: Boolean = false

	@ConfigOption(name = "Tag Outline Color", desc = "Color to use when highlighting players with /wpcmod tag command")
	@ConfigEditorColour
	var tagColor = ChromaColour(1f, 1f, 1f, 0, 0xFF)

	@Transient
	@ConfigOption(name = "Command Keybind Editor", desc = "Open the screen to manage command keybinds")
	@ConfigEditorButton(buttonText = "Open")
	val shortcutEditor = Runnable {
		MinecraftClient.getInstance().setScreen(ShortcutScreen())
	}

	@Accordion
	@ConfigOption(name = "Scrollable Tooltips", desc = "")
	var scrollableTooltips = ScrollableTooltips()

	class ScrollableTooltips {

		@ConfigOption(name = "Scroll Speed", desc = "Set the speed which the tooltip scrolls at")
		@ConfigEditorSlider(minStep = 1.0f, minValue = 1.0f, maxValue = 20.0f)
		var scrollSpeed: Float = 10f

		@ConfigOption(name = "Inverted Scroll", desc = "Invert the scroll direction")
		@ConfigEditorBoolean
		var invertedScroll: Boolean = false
	}

	@Accordion
	@ConfigOption(name = "Discard Highlighter", desc = "")
	var discard = DiscardSettings()

	class DiscardSettings {

		@ConfigOption(name = "Discard Highlighter", desc = "Highlights items based on the RegEx input")
		@ConfigEditorBoolean
		var highlighter: Boolean = false

		@ConfigOption(name = "Search RegEx", desc = "The RegEx to use when searching for items")
		@ConfigEditorText
		var regex: String = "(Bank|No Pain No Gain|Combo|Feather Falling|Infinite Quiver|Ultimate Jerry) (I*V*I)"
	}

	@Category(name = "Experiments", desc = "Experimentation Config")
	var experiments: ExperimentConfig = ExperimentConfig()
}