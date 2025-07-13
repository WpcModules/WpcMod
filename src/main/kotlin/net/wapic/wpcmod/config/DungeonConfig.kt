package net.wapic.wpcmod.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorColour
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class DungeonConfig {

    @Expose
    @ConfigOption(name = "Auto Close Chests", desc = "Automatically close secret chests")
    @ConfigEditorBoolean
    var autoCloseChests: Boolean = false

    @Expose
    @ConfigOption(name = "Alert on Talisman", desc = "Alerts you when secret chests contain a treasure talisman")
    @ConfigEditorBoolean
    var alertOnTreasureTalismans: Boolean = false

    @Expose
    @ConfigOption(name = "Discard Highlighter", desc = "Highlight junk dungeon reward items while holding Left Control")
    @ConfigEditorBoolean
    var discardHighlighter: Boolean = false

    @Expose
    @Accordion
    @ConfigOption(name = "Starred Mob ESP", desc = "")
    var starMobESP: EspSettings = EspSettings()

    class EspSettings {

        @Expose
        @ConfigOption(name = "Glow ESP", desc = "render a glow around the entity")
        @ConfigEditorBoolean
        var glow: Boolean = false

        @Expose
        @ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on this entity")
        @ConfigEditorColour
        var color = ChromaColour(1f, 0f, 0f, 0, 0xff)

    }
}