package net.wapic.wpcmod.config.dungeon

import io.github.notenoughupdates.moulconfig.annotations.*
import net.wapic.wpcmod.config.components.GlowableESPConfig
import net.wapic.wpcmod.features.instance.AutoGFS

class DungeonConfig {

	@ConfigOption(name = "Auto Close Chests", desc = "Automatically close secret chests")
	@ConfigEditorBoolean
	var autoCloseChests: Boolean = false

	@ConfigOption(name = "Alert on Treasure Talisman", desc = "Alerts when a treasure talisman spawns nearby")
	@ConfigEditorBoolean
	var alertOnTreasureTalismans: Boolean = false

	@ConfigOption(name = "Spirit Bear Timer", desc = "Show a timer until spirit bear spawns")
	@ConfigEditorBoolean
	var spiritBear: Boolean = false

	@ConfigOption(name = "Cancel Interact", desc = "Cancel block interaction with ender pearls")
	@ConfigEditorBoolean
	var cancelInteract: Boolean = false

	@ConfigOption(
		name = "Easy Superboom",
		desc = "Select and place superboom from the hotbar using the pick block keybind"
	)
	@ConfigEditorBoolean
	var easySuperboom: Boolean = false

	@ConfigOption(
		name = "Auto Show Extra Stats",
		desc = "Automatically run /showextrastats at the end of a run"
	)
	@ConfigEditorBoolean
	var autoShowExtraStats: Boolean = false

	@Accordion
	@ConfigOption(name = "Livid Solver", desc = "")
	var lividSolver: LividSolverConfig = LividSolverConfig()

	class LividSolverConfig : GlowableESPConfig() {

		@ConfigOption(name = "Use Livid Color", desc = "Use the color of livid instead of a predetermined color")
		@ConfigEditorBoolean
		var useLividColor = false
	}

	@Accordion
	@ConfigOption(name = "Dungeonbreaker", desc = "")
	var dungeonbreaker: DungeonbreakerConfig = DungeonbreakerConfig()

	class DungeonbreakerConfig {
		@ConfigOption(
			name = "Zero Ping Dungeon Breaker",
			desc = "Sets the block you're breaking to AIR to remove the delay"
		)
		@ConfigEditorBoolean
		var zeroPingDB = false

		@ConfigOption(
			name = "Prevent Breaking Secrets",
			desc = "Prevents blocks in the list below from being broken with Dungeonbreaker"
		)
		@ConfigEditorBoolean
		var preventBreakingSecrets = false

		@ConfigOption(
			name = "Prevented Secrets",
			desc = "Prevent breaking the selected blocks with Dungeonbreaker"
		)
		@ConfigEditorDraggableList
		var preventedDungeonbreakerBlocks = mutableListOf<InteractableBlocks>()
	}

	@Accordion
	@ConfigOption(name = "Bigger Hitboxes", desc = "")
	var hitboxes: HitBoxConfig = HitBoxConfig()

	class HitBoxConfig {
		@ConfigOption(name = "Enable Bigger Hitboxes", desc = "Enables bigger hitboxes on blocks selected below")
		@ConfigEditorBoolean
		var enabled = false

		@ConfigOption(name = "Bigger Hitboxes", desc = "Increase the hitbox of the selected blocks")
		@ConfigEditorDraggableList
		var blocks = mutableListOf<InteractableBlocks>()
	}

	@Accordion
	@ConfigOption(name = "Auto GFS", desc = "")
	var autoGFS: AutoGetFromSack = AutoGetFromSack()

	class AutoGetFromSack {
		@ConfigOption(
			name = "Enable Auto GFS",
			desc = "Enables automatically getting items from sack at the start of dungeon run"
		)
		@ConfigEditorBoolean
		var enabled = false

		@ConfigOption(name = "", desc = "Items to automatically get from sack")
		@ConfigEditorDraggableList
		var items = mutableListOf<AutoGFS.DungeonSackItems>()
	}

	@Accordion
	@ConfigOption(
		name = "Invincibility Items",
		desc = "Cooldown timers and alerts for items which give you invincibility"
	)
	var invincibilityTimer: InvincibilityTimerConfig = InvincibilityTimerConfig()

	class InvincibilityTimerConfig {
		@ConfigOption(name = "Enable Invincibility Timers", desc = "Enables invincibility timer features")
		@ConfigEditorBoolean
		var enabled = false

		@ConfigOption(name = "Show Hud", desc = "Show cooldown in a HUD element")
		@ConfigEditorBoolean
		var hud = false

		@ConfigOption(name = "Send Chat Message", desc = "Send a chat message when items activate")
		@ConfigEditorBoolean
		var message = false

		@ConfigOption(name = "Show Title", desc = "Show a title when items activate")
		@ConfigEditorBoolean
		var title = false
	}

	enum class InteractableBlocks(val label: String) {
		LEVER("Lever"),
		BUTTON("Button"),
		CHEST("Chest"),
		SKULL("Skull");

		override fun toString(): String = "§f$label"
	}

	@Category(name = "ESP", desc = "Configure ESP on dungeon mobs")
	var esp: ESPConfig = ESPConfig()

	@Category(name = "FunnyMap", desc = "funny map")
	var funnyMap: FunnyConfig = FunnyConfig()

	@Category(name = "Floor 7", desc = "Floor 7 features")
	var floor7: Floor7Config = Floor7Config()

	@Category(name = "Score Calculation", desc = "Configure score calculation")
	var scoreCalculation: ScoreCalculationConfig = ScoreCalculationConfig()
}