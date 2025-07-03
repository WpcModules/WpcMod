package net.wapic.wpcmod.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class GeneralConfig {

    @Expose
    @JvmField
    @ConfigOption(name = "Fullbright", desc = "Makes things not dark")
    @ConfigEditorBoolean
    var fullbright: Boolean = false
}