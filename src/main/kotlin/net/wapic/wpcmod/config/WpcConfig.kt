package net.wapic.wpcmod.config

import io.github.notenoughupdates.moulconfig.Config
import io.github.notenoughupdates.moulconfig.Social
import io.github.notenoughupdates.moulconfig.annotations.Category
import io.github.notenoughupdates.moulconfig.common.MyResourceLocation
import io.github.notenoughupdates.moulconfig.common.text.StructuredText
import net.fabricmc.loader.api.FabricLoader
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.chat.ChatConfig
import net.wapic.wpcmod.config.dev.DevConfig
import net.wapic.wpcmod.config.dungeon.DungeonConfig
import net.wapic.wpcmod.config.end.EndConfig
import net.wapic.wpcmod.config.events.EventsConfig
import net.wapic.wpcmod.config.fishing.FishingConfig
import net.wapic.wpcmod.config.galatea.GalateaConfig
import net.wapic.wpcmod.config.garden.GardenConfig
import net.wapic.wpcmod.config.general.GeneralConfig
import net.wapic.wpcmod.config.inventory.InventoryConfig
import net.wapic.wpcmod.config.kuudra.KuudraConfig
import net.wapic.wpcmod.config.mining.MiningConfig
import net.wapic.wpcmod.config.render.RenderConfig
import net.wapic.wpcmod.config.slayer.SlayerConfig

class WpcConfig : Config() {

	override fun getTitle(): StructuredText {
		return StructuredText.of("§bWpcMod ${WpcMod.version}§r mc-${FabricLoader.getInstance().rawGameVersion}")
	}

	override fun getSocials(): List<Social> {
		val github = Social.forLink(
			StructuredText.of("WpcMod GitHub Page"),
			MyResourceLocation.parse("wpcmod:github-mark-white.png"),
			"https://github.com/WpcModules/WpcMod"
		)

		val gitea = Social.forLink(
			StructuredText.of("WpcMod Gitea Page"),
			MyResourceLocation.parse("wpcmod:gitea-logo.png"),
			"https://git.wapic.net/wapic/WpcMod"
		)
		return listOf(gitea, github)
	}

	override fun saveNow() {
		ConfigManager.saveConfig()
	}

	@Category(name = "General", desc = "General configurations that don't fit into other categories")
	var general: GeneralConfig = GeneralConfig()

	@Category(name = "Galatea", desc = "Configuration options for Galatea")
	var galatea: GalateaConfig = GalateaConfig()

	@Category(name = "End", desc = "Configuration options for End")
	var end: EndConfig = EndConfig()

	@Category(name = "Fishing", desc = "Configuration options for fishing")
	var fishing: FishingConfig = FishingConfig()

	@Category(name = "Kuudra", desc = "Configuration options for Kuudra")
	var kuudra: KuudraConfig = KuudraConfig()

	@Category(name = "Dungeons", desc = "Configuration options for Dungeons")
	var dungeon: DungeonConfig = DungeonConfig()

	@Category(name = "Slayer", desc = "Configuration options for Slayers")
	var slayer: SlayerConfig = SlayerConfig()

	@Category(name = "Mining", desc = "Features that involve Dwarven Mines, Crystal Hollows, Glacite tunnels")
	var mining: MiningConfig = MiningConfig()

	@Category(name = "Garden", desc = "Configuration options for features in the Garden")
	var garden: GardenConfig = GardenConfig()

	@Category(name = "Chat", desc = "Configuration options for Chat")
	var chat: ChatConfig = ChatConfig()

	@Category(name = "Events", desc = "Configuration options for SkyBlock Events")
	var events: EventsConfig = EventsConfig()

	@Category(name = "Inventory", desc = "Features that interact mainly with the inventory")
	var inventory: InventoryConfig = InventoryConfig()

	@Category(name = "Render", desc = "Features that handle any rendering options")
	var render: RenderConfig = RenderConfig()

	@Category(name = "Dev", desc = "Development features")
	var dev: DevConfig = DevConfig()
}