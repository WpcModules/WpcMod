package net.wapic.wpcmod.config.galatea

import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorColour
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class ESPConfig {

	@Accordion
	@ConfigOption(name = "Shulker", desc = "Shulker Settings")
	var shulker = ShulkerConfig()

	class ShulkerConfig() {

		@ConfigOption(name = "Glow", desc = "Render a glow around the entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@ConfigOption(name = "Color", desc = "The color to use for glow, box, and tracer.")
		@ConfigEditorColour
		var color = ChromaColour(0f, 1f, 1f, 0, 0xFF)

		@ConfigOption(name = "Box", desc = "Draw a box around the entity")
		@ConfigEditorBoolean
		var box: Boolean = false

		@ConfigOption(name = "Tracer", desc = "Draw a line from your crosshair to the entity")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}

	@Accordion
	@ConfigOption(name = "Panda", desc = "Panda Settings")
	var panda = PandaConfig()

	class PandaConfig() {

		@ConfigOption(name = "Glow", desc = "Render a glow around the entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@ConfigOption(name = "Color", desc = "The color to use for glow, box, and tracer.")
		@ConfigEditorColour
		var color = ChromaColour(0f, 1f, 1f, 0, 0xFF)

		@ConfigOption(name = "Box", desc = "Draw a box around the entity")
		@ConfigEditorBoolean
		var box: Boolean = false

		@ConfigOption(name = "Tracer", desc = "Draw a line from your crosshair to the entity")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}

	@Accordion
	@ConfigOption(name = "Frog", desc = "Frog Settings")
	var frog = FrogConfig()

	class FrogConfig() {

		@ConfigOption(name = "Glow", desc = "Render a glow around the entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@ConfigOption(name = "Color", desc = "sets the colour for all on this entity")
		@ConfigEditorColour
		var color = ChromaColour(0f, 1f, 1f, 0, 0xFF)

		@ConfigOption(name = "Box", desc = "Draw a box around the entity")
		@ConfigEditorBoolean
		var box: Boolean = false

		@ConfigOption(name = "Tracer", desc = "Draw a line from your crosshair to the entity")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}

	@Accordion
	@ConfigOption(name = "Axolotl", desc = "Axolotl Settings")
	var axolotl = AxolotlConfig()

	class AxolotlConfig() {

		@ConfigOption(name = "Glow", desc = "Render a glow around the entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@ConfigOption(name = "Color", desc = "sets the colour for all on this entity")
		@ConfigEditorColour
		var color = ChromaColour(0f, 1f, 1f, 0, 0xFF)

		@ConfigOption(name = "Box", desc = "Draw a box around the entity")
		@ConfigEditorBoolean
		var box: Boolean = false

		@ConfigOption(name = "Tracer", desc = "Draw a line from your crosshair to the entity")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}

	@Accordion
	@ConfigOption(name = "Pufferfish", desc = "Pufferfish Settings")
	var pufferfish = PufferfishConfig()

	class PufferfishConfig() {

		@ConfigOption(name = "Glow", desc = "Render a glow around the entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@ConfigOption(name = "Color", desc = "sets the colour for all on this entity")
		@ConfigEditorColour
		var color = ChromaColour(0f, 1f, 1f, 0, 0xFF)

		@ConfigOption(name = "Box", desc = "Draw a box around the entity")
		@ConfigEditorBoolean
		var box: Boolean = false

		@ConfigOption(name = "Tracer", desc = "Draw a line from your crosshair to the entity")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}

	@Accordion
	@ConfigOption(name = "Shellwise", desc = "Shellwise Settings")
	var shellwise = ShellwiseConfig()

	class ShellwiseConfig() {

		@ConfigOption(name = "Glow", desc = "Render a glow around the entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@ConfigOption(name = "Color", desc = "sets the colour for all on this entity")
		@ConfigEditorColour
		var color = ChromaColour(0f, 1f, 1f, 0, 0xFF)

		@ConfigOption(name = "Box", desc = "Draw a box around the entity")
		@ConfigEditorBoolean
		var box: Boolean = false

		@ConfigOption(name = "Tracer", desc = "Draw a line from your crosshair to the entity")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}

	@Accordion
	@ConfigOption(name = "Invisibug", desc = "Invisibug Settings")
	var invisibug = InvisibugConfig()

	class InvisibugConfig() {

		@ConfigOption(name = "Color", desc = "sets the colour for all on the entity")
		@ConfigEditorColour
		var color = ChromaColour(1f, 1f, 1f, 0, 0xFF)

		@ConfigOption(name = "Box", desc = "Draw a box around the entity")
		@ConfigEditorBoolean
		var box: Boolean = false

		@ConfigOption(name = "Tracer", desc = "Draw a line from your crosshair to the entity")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}

	@Accordion
	@ConfigOption(name = "Forest Node", desc = "Forest Node Settings")
	var forestNode = ForestNodeConfig()

	class ForestNodeConfig() {

		@ConfigOption(name = "Color", desc = "sets the colour for all on the node")
		@ConfigEditorColour
		var color = ChromaColour(1f, 1f, 1f, 0, 0xFF)

		@ConfigOption(name = "Box", desc = "Draw a box around the node")
		@ConfigEditorBoolean
		var box: Boolean = false

		@ConfigOption(name = "Tracer", desc = "Draw a line from your crosshair to the node")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}
}