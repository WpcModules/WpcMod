package net.wapic.wpcmod.config.inventory

import io.github.notenoughupdates.moulconfig.annotations.*

class InventoryConfig {

	@ConfigOption(name = "Armor Swapper", desc = "Instantly swap to Sorrow when Keybind is hit")
	@ConfigEditorBoolean
	var armorSwapper: Boolean = false

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