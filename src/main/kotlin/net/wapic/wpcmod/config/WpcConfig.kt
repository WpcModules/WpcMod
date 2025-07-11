package net.wapic.wpcmod.config

import io.github.notenoughupdates.moulconfig.Config
import io.github.notenoughupdates.moulconfig.annotations.Category
import net.wapic.wpcmod.WpcMod

class WpcConfig : Config() {

    override fun getTitle(): String {
        return "§bWpcMod ${WpcMod.version}§r"
    }

    @Category(name = "General", desc = "General configurations that don't fit into other categories")
    var generalConfig: GeneralConfig = GeneralConfig()

    @Category(name = "Galatea", desc = "Configuration options for Galatea")
    var galateaConfig: GalateaConfig = GalateaConfig()

    @Category(name = "Kuudra", desc = "Configuration options for Kuudra")
    var kuudraConfig: KuudraConfig = KuudraConfig()

    @Category(name = "Dungeons", desc = "Configuration options for Dungeons")
    var dungeonConfig: DungeonConfig = DungeonConfig()
}