package net.wapic.wpcmod.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.*

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
    @Accordion
    @ConfigOption(name = "Auto GFS Settings", desc = "")
    var autoGFS: AutoGetFromSack = AutoGetFromSack()

    class AutoGetFromSack {

        @Expose
        @ConfigOption(name = "Ender Pearls", desc = "")
        @ConfigEditorBoolean
        var enderPearl = false

        @Expose
        @ConfigOption(name = "Superboom TNT", desc = "")
        @ConfigEditorBoolean
        var superboomTNT = false

        @Expose
        @ConfigOption(name = "Spirit Leaps", desc = "")
        @ConfigEditorBoolean
        var spiritLeap = false
    }

    @Expose
    @Category(name = "ESP", desc = "")
    var espSettings: EspSettings = EspSettings()

    class EspSettings {

        @Expose
        @Accordion
        @ConfigOption(name = "Starred Mob ESP", desc = "Starred Mob ESP Settings")
        var starMobESP = StarMobESPSettings()

        class StarMobESPSettings() {

            @Expose
            @ConfigOption(name = "Glow ESP", desc = "render a glow around the entity")
            @ConfigEditorBoolean
            var glow: Boolean = false

            @Expose
            @ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on this entity")
            @ConfigEditorColour
            var color = ChromaColour(1f, 0f, 0f, 0, 0xff)
        }

        @Expose
        @Accordion
        @ConfigOption(name = "Bat ESP", desc = "Bat ESP Settings")
        var batESP = BatESP()

        class BatESP() {

            @Expose
            @ConfigOption(name = "Glow ESP", desc = "render a glow around the entity")
            @ConfigEditorBoolean
            var glow: Boolean = false

            @Expose
            @ConfigOption(name = "ESP Color", desc = "sets the colour for all ESP on this entity")
            @ConfigEditorColour
            var color = ChromaColour(1f, 0f, 0f, 0, 0xff)
        }

        @Expose
        @Accordion
        @ConfigOption(name = "Miniboss ESP", desc = "Miniboss ESP Settings")
        var miniESP = MiniESP()

        class MiniESP() {

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
}