package net.wapic.wpcmod.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.Category
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorColour
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class KuudraConfig {

    @Expose
    @ConfigOption(name = "Auto GFS", desc = "Automatically retrieve ender pearls from sack using /gfs")
    @ConfigEditorBoolean
    var autoGfs: Boolean = false

    @Expose
    @Category(name = "ESP", desc = "")
    var espSettings: EspSettings = EspSettings()

    class EspSettings {

        @Expose
        @Accordion
        @ConfigOption(name = "Kuudra ESP", desc = "Kuudra ESP Settings")
        var kuudraSettings = KuudraSettings()

        class KuudraSettings() {

            @Expose
            @ConfigOption(name = "Glow ESP", desc = "render a glow around the entity")
            @ConfigEditorBoolean
            var glow: Boolean = false

            @Expose
            @ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on this entity")
            @ConfigEditorColour
            var color = ChromaColour(1f, 0f, 1f, 0, 0xFF)

            @Expose
            @ConfigOption(name = "Tracer", desc = "draw a line from your crosshair to the entity")
            @ConfigEditorBoolean
            var tracer: Boolean = false
        }
    }
}