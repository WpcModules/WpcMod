package net.wapic.wpcmod.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.Config
import io.github.notenoughupdates.moulconfig.annotations.Category
import net.wapic.wpcmod.WpcMod

class WpcConfig : Config() {

	override fun getTitle(): String {
		return "§bWpcMod ${WpcMod.version}§r"
	}

	override fun saveNow() {
		ConfigManager.saveConfig()
	}

	@Expose
	@Category(name = "General", desc = "General configurations that don't fit into other categories")
	var generalConfig: GeneralConfig = GeneralConfig()

	@Expose
	@Category(name = "Galatea", desc = "Configuration options for Galatea")
	var galateaConfig: GalateaConfig = GalateaConfig()

	@Expose
	@Category(name = "End", desc = "Configuration options for End")
	var endConfig: EndConfig = EndConfig()

	@Expose
	@Category(name = "Kuudra", desc = "Configuration options for Kuudra")
	var kuudraConfig: KuudraConfig = KuudraConfig()

	@Expose
	@Category(name = "Dungeons", desc = "Configuration options for Dungeons")
	var dungeonConfig: DungeonConfig = DungeonConfig()

    @Expose
    @Category(name = "Dev", desc = "Development features")
    var devConfig: DevConfig = DevConfig()
	@Expose
	@Category(name = "Dev", desc = "Development features")
	var devConfig: DevConfig = DevConfig()
}