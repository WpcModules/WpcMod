package net.wapic.wpcmod.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.Category
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorColour
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import net.wapic.wpcmod.config.GalateaConfig.EspSettings

class KuudraConfig {

    @Expose
    @JvmField
    @ConfigOption(name = "Auto GFS", desc = "Automatically retrieve ender pearls from sack using /gfs")
    @ConfigEditorBoolean
    var autoGfs: Boolean = false

    @Expose
    @JvmField
    @Category(name = "ESP", desc = "")
    var espSettings: EspSettings = EspSettings()

    class EspSettings {

        @Expose
        @JvmField
        @Accordion
        @ConfigOption(name = "Shulker ESP", desc = "Shulker ESP Settings")
        var kuudraSetings = KuudraSettings()

        class KuudraSettings() {

            @Expose
            @JvmField
            @ConfigOption(name = "Glow ESP", desc = "render a glow around the entity")
            @ConfigEditorBoolean
            var glow: Boolean = false

            @Expose
            @JvmField
            @ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on this entity")
            @ConfigEditorColour
            var color = ChromaColour(1f, 0f, 1f, 0, 0xFF)

            @Expose
            @JvmField
            @ConfigOption(name = "Tracer", desc = "draw a line from your crosshair to the entity")
            @ConfigEditorBoolean
            var tracer: Boolean = false
        }
    }
}