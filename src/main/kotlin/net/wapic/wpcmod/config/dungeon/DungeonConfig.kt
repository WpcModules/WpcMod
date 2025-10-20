package net.wapic.wpcmod.config.dungeon

import io.github.notenoughupdates.moulconfig.annotations.*

class DungeonConfig {

	@ConfigOption(name = "Auto Close Chests", desc = "Automatically close secret chests")
	@ConfigEditorBoolean
	var autoCloseChests: Boolean = false

	@ConfigOption(name = "Alert on Talisman", desc = "Alerts you when secret chests contain a treasure talisman")
	@ConfigEditorBoolean
	var alertOnTreasureTalismans: Boolean = false

	@ConfigOption(name = "Spirit Bear Timer", desc = "Show a timer until spirit bear spawns")
	@ConfigEditorBoolean
	var spiritBear: Boolean = false

	@Accordion
	@ConfigOption(name = "Hitboxes", desc = "Enable bigger hitboxes on certain blocks")
	var hitboxes: BiggerHitboxes = BiggerHitboxes()

	class BiggerHitboxes {

		@ConfigOption(name = "Lever", desc = "")
		@ConfigEditorBoolean
		var lever = false

		@ConfigOption(name = "Button", desc = "")
		@ConfigEditorBoolean
		var button = false
	}

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

	@Accordion
	@ConfigOption(name = "Score Calculation", desc = "")
	var scoreCalculation: ScoreCalculationConfig = ScoreCalculationConfig()

	class ScoreCalculationConfig {

		@ConfigOption(name = "Score Estimate", desc = "Show dungeon score in a HUD element")
		@ConfigEditorDropdown
		var scoreEstimate: ScoreHudType = ScoreHudType.DISABLED

		@ConfigOption(name = "Mimic Message", desc = "Alert in chat when mimic has been killed\nRequires Score Calculation to be active")
		@ConfigEditorBoolean
		var mimicMessage: Boolean = false

		@ConfigOption(name = "Assume Paul", desc = "Assume Paul is active Mayor with +10 bonus score")
		@ConfigEditorBoolean
		var assumePaul: Boolean = false

		enum class ScoreHudType {
			DISABLED,
			MINIMIZED,
			FULL,
		}
	}

	@Category(name = "ESP", desc = "Configure ESP on Dungeon mobs")
	var esp: ESPConfig = ESPConfig()

	@Category(name = "FunnyMap", desc = "funny map")
	var funnyMap: FunnyConfig = FunnyConfig()

	@Category(name = "Floor 7", desc = "Floor 7 Features")
	var floor7: Floor7Config = Floor7Config()
}