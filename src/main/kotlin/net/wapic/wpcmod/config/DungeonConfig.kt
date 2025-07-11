package net.wapic.wpcmod.config

import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class DungeonConfig {

    @ConfigOption(name = "Auto Close Chests", desc = "Automatically close secret chests")
    @ConfigEditorBoolean
    var autoCloseChests: Boolean = false

    @ConfigOption(name = "Alert on Talisman", desc = "Alerts you when secret chests contain a treasure talisman")
    @ConfigEditorBoolean
    var alertOnTreasureTalismans: Boolean = false
}