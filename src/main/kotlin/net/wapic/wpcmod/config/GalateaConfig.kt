package net.wapic.wpcmod.config

import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.Category
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorColour
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class GalateaConfig {

    @Category(name = "ESP", desc = "")
    var espSettings: EspSettings = EspSettings()

    class EspSettings {

        @Accordion
        @ConfigOption(name = "Shulker ESP", desc = "Shulker ESP Settings")
        var shulkerSettings = ShulkerSettings()

        class ShulkerSettings() {

            @ConfigOption(name = "Glow ESP", desc = "render a glow around the entity")
            @ConfigEditorBoolean
            var glow: Boolean = false

            @ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on this entity")
            @ConfigEditorColour
            var color = ChromaColour(0f, 1f, 1f, 0, 0xFF)

            @ConfigOption(name = "Box ESP", desc = "draw an ESP Box around the entity")
            @ConfigEditorBoolean
            var box: Boolean = false

            @ConfigOption(name = "Tracer", desc = "draw a line from your crosshair to the entity")
            @ConfigEditorBoolean
            var tracer: Boolean = false
        }

        @Accordion
        @ConfigOption(name = "Panda ESP", desc = "Panda ESP Settings")
        var pandaSettings = PandaSettings()

        class PandaSettings() {

            @ConfigOption(name = "Glow ESP", desc = "render a glow around the entity")
            @ConfigEditorBoolean
            var glow: Boolean = false

            @ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on this entity")
            @ConfigEditorColour
            var color = ChromaColour(0f, 1f, 1f, 0, 0xFF)

            @ConfigOption(name = "Box ESP", desc = "draw an ESP Box around the entity")
            @ConfigEditorBoolean
            var box: Boolean = false

            @ConfigOption(name = "Tracer", desc = "draw a line from your crosshair to the entity")
            @ConfigEditorBoolean
            var tracer: Boolean = false
        }

        @Accordion
        @ConfigOption(name = "Frog ESP", desc = "Frog ESP Settings")
        var frogSettings = FrogSettings()

        class FrogSettings() {

            @ConfigOption(name = "Glow ESP", desc = "render a glow around the entity")
            @ConfigEditorBoolean
            var glow: Boolean = false

            @ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on this entity")
            @ConfigEditorColour
            var color = ChromaColour(0f, 1f, 1f, 0, 0xFF)

            @ConfigOption(name = "Box ESP", desc = "draw an ESP Box around the entity")
            @ConfigEditorBoolean
            var box: Boolean = false

            @ConfigOption(name = "Tracer", desc = "draw a line from your crosshair to the entity")
            @ConfigEditorBoolean
            var tracer: Boolean = false
        }

        @Accordion
        @ConfigOption(name = "Axolotl ESP", desc = "Axolotl ESP Settings")
        var axolotlSettings = AxolotlSettings()

        class AxolotlSettings() {

            @ConfigOption(name = "Glow ESP", desc = "render a glow around the entity")
            @ConfigEditorBoolean
            var glow: Boolean = false

            @ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on this entity")
            @ConfigEditorColour
            var color = ChromaColour(0f, 1f, 1f, 0, 0xFF)

            @ConfigOption(name = "Box ESP", desc = "draw an ESP Box around the entity")
            @ConfigEditorBoolean
            var box: Boolean = false

            @ConfigOption(name = "Tracer", desc = "draw a line from your crosshair to the entity")
            @ConfigEditorBoolean
            var tracer: Boolean = false
        }

        @Accordion
        @ConfigOption(name = "Pufferfish ESP", desc = "Pufferfish ESP Settings")
        var pufferfishSettings = PufferfishSettings()

        class PufferfishSettings() {

            @ConfigOption(name = "Glow ESP", desc = "render a glow around the entity")
            @ConfigEditorBoolean
            var glow: Boolean = false

            @ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on this entity")
            @ConfigEditorColour
            var color = ChromaColour(0f, 1f, 1f, 0, 0xFF)

            @ConfigOption(name = "Box ESP", desc = "draw an ESP Box around the entity")
            @ConfigEditorBoolean
            var box: Boolean = false

            @ConfigOption(name = "Tracer", desc = "draw a line from your crosshair to the entity")
            @ConfigEditorBoolean
            var tracer: Boolean = false
        }

        @Accordion
        @ConfigOption(name = "Invisibug ESP", desc = "Invisibug ESP Settings")
        var invisibugSettings = InvisibugSettings()

        class InvisibugSettings() {

            @ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on the entity")
            @ConfigEditorColour
            var color = ChromaColour(1f, 1f, 1f, 0, 0xFF)

            @ConfigOption(name = "Box ESP", desc = "draw an ESP Box around the entity")
            @ConfigEditorBoolean
            var box: Boolean = false

            @ConfigOption(name = "Tracer", desc = "draw a line from your crosshair to the entity")
            @ConfigEditorBoolean
            var tracer: Boolean = false
        }

        @Accordion
        @ConfigOption(name = "Forest Node ESP", desc = "Forest Node ESP Settings")
        var forestNodeSettings = ForestNodeSettings()

        class ForestNodeSettings() {

            @ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on the node")
            @ConfigEditorColour
            var color = ChromaColour(1f, 1f, 1f, 0, 0xFF)

            @ConfigOption(name = "Box ESP", desc = "draw an ESP Box around the node")
            @ConfigEditorBoolean
            var box: Boolean = false

            @ConfigOption(name = "Tracer", desc = "draw a line from your crosshair to the node")
            @ConfigEditorBoolean
            var tracer: Boolean = false
        }
    }
}