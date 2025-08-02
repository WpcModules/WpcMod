package net.wapic.wpcmod.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.Config
import io.github.notenoughupdates.moulconfig.Social
import io.github.notenoughupdates.moulconfig.annotations.Category
import io.github.notenoughupdates.moulconfig.common.MyResourceLocation
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.chat.ChatConfig
import net.wapic.wpcmod.config.dev.DevConfig
import net.wapic.wpcmod.config.dungeon.DungeonConfig
import net.wapic.wpcmod.config.end.EndConfig
import net.wapic.wpcmod.config.galatea.GalateaConfig
import net.wapic.wpcmod.config.general.GeneralConfig
import net.wapic.wpcmod.config.kuudra.KuudraConfig

class WpcConfig : Config() {

	override fun getTitle(): String {
		return "§bWpcMod ${WpcMod.version}§r"
	}

	override fun getSocials(): List<Social> {
		val github = Social.forLink(
			"WpcMod GitHub Page",
			MyResourceLocation.parse("wpcmod:github-mark-white.png"),
			"https://github.com/WpcModules/WpcMod"
		)
		return listOf(github)
	}

	override fun saveNow() {
		ConfigManager.saveConfig()
	}

	@Expose
	@Category(name = "General", desc = "General configurations that don't fit into other categories")
	var general: GeneralConfig = GeneralConfig()

	@Expose
	@Category(name = "Galatea", desc = "Configuration options for Galatea")
	var galatea: GalateaConfig = GalateaConfig()

	@Expose
	@Category(name = "End", desc = "Configuration options for End")
	var end: EndConfig = EndConfig()

	@Expose
	@Category(name = "Kuudra", desc = "Configuration options for Kuudra")
	var kuudra: KuudraConfig = KuudraConfig()

	@Expose
	@Category(name = "Dungeons", desc = "Configuration options for Dungeons")
	var dungeon: DungeonConfig = DungeonConfig()

	@Expose
	@Category(name = "Chat", desc = "Configuration options for Chat")
	var chat: ChatConfig = ChatConfig()

	@Expose
	@Category(name = "Dev", desc = "Development features")
	var dev: DevConfig = DevConfig()
}