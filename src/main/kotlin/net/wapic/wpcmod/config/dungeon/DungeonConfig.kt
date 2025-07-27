package net.wapic.wpcmod.config.dungeon

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.Category
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
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
	var esp: ESPConfig = ESPConfig()
}