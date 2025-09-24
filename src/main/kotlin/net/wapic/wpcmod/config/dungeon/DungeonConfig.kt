package net.wapic.wpcmod.config.dungeon

import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.Category
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class DungeonConfig {

	@ConfigOption(name = "Score Calculator", desc = "Show dungeon score in a HUD")
	@ConfigEditorBoolean
	var scoreCalculation: Boolean = false

	@ConfigOption(name = "Mimic Message", desc = "Alert in chat when mimic has been killed")
	@ConfigEditorBoolean
	var mimicMessage: Boolean = false

	@ConfigOption(name = "Auto Close Chests", desc = "Automatically close secret chests")
	@ConfigEditorBoolean
	var autoCloseChests: Boolean = false

	@ConfigOption(name = "Alert on Talisman", desc = "Alerts you when secret chests contain a treasure talisman")
	@ConfigEditorBoolean
	var alertOnTreasureTalismans: Boolean = false

	@Accordion
	@ConfigOption(name = "Auto GFS", desc = "Automatically run /getfromsack for these items on dungeon start")
	var autoGFS: AutoGetFromSack = AutoGetFromSack()

	class AutoGetFromSack {

		@ConfigOption(name = "Ender Pearls", desc = "")
		@ConfigEditorBoolean
		var enderPearl = false

		@ConfigOption(name = "Superboom TNT", desc = "")
		@ConfigEditorBoolean
		var superboomTNT = false

		@ConfigOption(name = "Spirit Leaps", desc = "")
		@ConfigEditorBoolean
		var spiritLeap = false
	}

	@Category(name = "ESP", desc = "Configure ESP on Dungeon mobs")
	var esp: ESPConfig = ESPConfig()
}