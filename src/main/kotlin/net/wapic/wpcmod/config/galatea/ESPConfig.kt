package net.wapic.wpcmod.config.galatea

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorColour
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class ESPConfig {

	@Expose
	@Accordion
	@ConfigOption(name = "Shulker ESP", desc = "Shulker ESP Settings")
	var shulker = ShulkerConfig()

	class ShulkerConfig() {

		@Expose
		@ConfigOption(name = "Glow ESP", desc = "render a glow around the entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@Expose
		@ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on this entity")
		@ConfigEditorColour
		var color = ChromaColour(0f, 1f, 1f, 0, 0xFF)

		@Expose
		@ConfigOption(name = "Box ESP", desc = "draw an ESP Box around the entity")
		@ConfigEditorBoolean
		var box: Boolean = false

		@Expose
		@ConfigOption(name = "Tracer", desc = "draw a line from your crosshair to the entity")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}

	@Expose
	@Accordion
	@ConfigOption(name = "Panda ESP", desc = "Panda ESP Settings")
	var panda = PandaConfig()

	class PandaConfig() {

		@Expose
		@ConfigOption(name = "Glow ESP", desc = "render a glow around the entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@Expose
		@ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on this entity")
		@ConfigEditorColour
		var color = ChromaColour(0f, 1f, 1f, 0, 0xFF)

		@Expose
		@ConfigOption(name = "Box ESP", desc = "draw an ESP Box around the entity")
		@ConfigEditorBoolean
		var box: Boolean = false

		@Expose
		@ConfigOption(name = "Tracer", desc = "draw a line from your crosshair to the entity")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}

	@Expose
	@Accordion
	@ConfigOption(name = "Frog ESP", desc = "Frog ESP Settings")
	var frog = FrogConfig()

	class FrogConfig() {

		@Expose
		@ConfigOption(name = "Glow ESP", desc = "render a glow around the entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@Expose
		@ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on this entity")
		@ConfigEditorColour
		var color = ChromaColour(0f, 1f, 1f, 0, 0xFF)

		@Expose
		@ConfigOption(name = "Box ESP", desc = "draw an ESP Box around the entity")
		@ConfigEditorBoolean
		var box: Boolean = false

		@Expose
		@ConfigOption(name = "Tracer", desc = "draw a line from your crosshair to the entity")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}

	@Expose
	@Accordion
	@ConfigOption(name = "Axolotl ESP", desc = "Axolotl ESP Settings")
	var axolotl = AxolotlConfig()

	class AxolotlConfig() {

		@Expose
		@ConfigOption(name = "Glow ESP", desc = "render a glow around the entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@Expose
		@ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on this entity")
		@ConfigEditorColour
		var color = ChromaColour(0f, 1f, 1f, 0, 0xFF)

		@Expose
		@ConfigOption(name = "Box ESP", desc = "draw an ESP Box around the entity")
		@ConfigEditorBoolean
		var box: Boolean = false

		@Expose
		@ConfigOption(name = "Tracer", desc = "draw a line from your crosshair to the entity")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}

	@Expose
	@Accordion
	@ConfigOption(name = "Pufferfish ESP", desc = "Pufferfish ESP Settings")
	var pufferfish = PufferfishConfig()

	class PufferfishConfig() {

		@Expose
		@ConfigOption(name = "Glow ESP", desc = "render a glow around the entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@Expose
		@ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on this entity")
		@ConfigEditorColour
		var color = ChromaColour(0f, 1f, 1f, 0, 0xFF)

		@Expose
		@ConfigOption(name = "Box ESP", desc = "draw an ESP Box around the entity")
		@ConfigEditorBoolean
		var box: Boolean = false

		@Expose
		@ConfigOption(name = "Tracer", desc = "draw a line from your crosshair to the entity")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}

	@Expose
	@Accordion
	@ConfigOption(name = "Shellwise ESP", desc = "Shellwise ESP Settings")
	var shellwise = ShellwiseConfig()

	class ShellwiseConfig() {

		@Expose
		@ConfigOption(name = "Glow ESP", desc = "render a glow around the entity")
		@ConfigEditorBoolean
		var glow: Boolean = false

		@Expose
		@ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on this entity")
		@ConfigEditorColour
		var color = ChromaColour(0f, 1f, 1f, 0, 0xFF)

		@Expose
		@ConfigOption(name = "Box ESP", desc = "draw an ESP Box around the entity")
		@ConfigEditorBoolean
		var box: Boolean = false

		@Expose
		@ConfigOption(name = "Tracer", desc = "draw a line from your crosshair to the entity")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}

	@Expose
	@Accordion
	@ConfigOption(name = "Invisibug ESP", desc = "Invisibug ESP Settings")
	var invisibug = InvisibugConfig()

	class InvisibugConfig() {

		@Expose
		@ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on the entity")
		@ConfigEditorColour
		var color = ChromaColour(1f, 1f, 1f, 0, 0xFF)

		@Expose
		@ConfigOption(name = "Box ESP", desc = "draw an ESP Box around the entity")
		@ConfigEditorBoolean
		var box: Boolean = false

		@Expose
		@ConfigOption(name = "Tracer", desc = "draw a line from your crosshair to the entity")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}

	@Expose
	@Accordion
	@ConfigOption(name = "Forest Node ESP", desc = "Forest Node ESP Settings")
	var forestNode = ForestNodeConfig()

	class ForestNodeConfig() {

		@Expose
		@ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on the node")
		@ConfigEditorColour
		var color = ChromaColour(1f, 1f, 1f, 0, 0xFF)

		@Expose
		@ConfigOption(name = "Box ESP", desc = "draw an ESP Box around the node")
		@ConfigEditorBoolean
		var box: Boolean = false

		@Expose
		@ConfigOption(name = "Tracer", desc = "draw a line from your crosshair to the node")
		@ConfigEditorBoolean
		var tracer: Boolean = false
	}
}