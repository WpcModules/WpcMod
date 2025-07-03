package net.wapic.wpcmod.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.Config
import io.github.notenoughupdates.moulconfig.annotations.Category

class WpcConfig : Config() {

    override fun getTitle(): String {
        return "§bWpcMod Config"
    }

    @Expose
    @JvmField
    @Category(name = "General", desc = "General configurations that don't fit into other categories")
    var generalConfig: GeneralConfig = GeneralConfig()

    @Expose
    @JvmField
    @Category(name = "Galatea", desc = "Configuration options for Galatea")
    var galateaConfig: GalateaConfig = GalateaConfig()

    @Expose
    @JvmField
    @Category(name = "Kuudra", desc = "Configuration options for Kuudra")
    var kuudraConfig: KuudraConfig = KuudraConfig()
}