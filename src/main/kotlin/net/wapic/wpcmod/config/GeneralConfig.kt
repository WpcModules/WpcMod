package net.wapic.wpcmod.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.*
import net.minecraft.client.MinecraftClient
import net.wapic.wpcmod.features.general.shortcut.ShortcutScreen

class GeneralConfig {

	@Expose
	@ConfigOption(name = "Fullbright", desc = "Makes things not dark")
	@ConfigEditorBoolean
	var fullbright: Boolean = false

	@Expose
	@ConfigOption(name = "Armor Swapper", desc = "Instantly swap to Sorrow when Keybind is hit")
	@ConfigEditorBoolean
	var armorSwapper: Boolean = false

	@Expose
	@ConfigOption(name = "Prevent Placing Items", desc = "Prevent placing items such as Weird Tuba and Flower of Truth")
	@ConfigEditorBoolean
	var preventPlacing: Boolean = false

	@Expose
	@ConfigOption(name = "No Front Camera", desc = "Disables the front facing camera from f5")
	@ConfigEditorBoolean
	var disableFrontCamera: Boolean = false

	@Expose
	@ConfigOption(name = "Tag Outline Color", desc = "Color to use when highlighting players with /wpcmod tag command")
	@ConfigEditorColour
	var tagColor = ChromaColour(1f, 1f, 1f, 0, 0xFF)

	@Expose
	@Transient
	@ConfigOption(name = "Command Keybind Editor", desc = "Opens the screen to manage command keybinds")
	@ConfigEditorButton(buttonText = "Open")
	val shortcutEditor = Runnable {
		MinecraftClient.getInstance().setScreen(ShortcutScreen())
	}

	@Expose
	@Accordion
	@ConfigOption(name = "Discard Highlighter", desc = "")
	var discardSettings = DiscardSettings()

	class DiscardSettings {

		@Expose
		@ConfigOption(name = "Discard Highlighter", desc = "Highlights items based on the RegEx input")
		@ConfigEditorBoolean
		var discardHighlighter: Boolean = false

		@Expose
		@ConfigOption(name = "Search RegEx", desc = "The RegEx to use when searching for items")
		@ConfigEditorText
		var discardRegex: String = "(Bank|No Pain No Gain|Combo|Feather Falling|Infinite Quiver|Ultimate Jerry) (I*V*I)"
	}

	@Expose
	@Category(name = "Experiments", desc = "Experimentation Config")
	var experimentSettings: ExperimentSettings = ExperimentSettings()

	class ExperimentSettings {

		@Expose
		@ConfigOption(name = "Auto Solve Experiments", desc = "Completes Chronomatron and Ultrasequencer automatically")
		@ConfigEditorBoolean
		var autoExperiments: Boolean = false

		@Expose
		@ConfigOption(name = "Superpairs Solver", desc = "Highlight items in Superpairs similar to NEU")
		@ConfigEditorBoolean
		var superpairsSolver: Boolean = false

		@Expose
		@ConfigOption(name = "Auto Close", desc = "Auto Close Experiment when done")
		@ConfigEditorBoolean
		var autoClose: Boolean = false

		@Expose
		@ConfigOption(name = "Click delay", desc = "Delay between clicks on experiments")
		@ConfigEditorSlider(minStep = 1.0f, minValue = 200.0f, maxValue = 500.0f)
		var clickDelay: Float = 250.0f

		@Expose
		@ConfigOption(name = "Metaphysical Serum", desc = "Sets how many Metaphysical serums have been consumed")
		@ConfigEditorSlider(minStep = 1.0f, minValue = 0.0f, maxValue = 3.0f)
		var serumCount: Float = 0.0f
	}
}