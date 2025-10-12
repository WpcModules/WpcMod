package net.wapic.wpcmod.features.funnymap.dungeon

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.funnymap.core.DungeonPlayer
import net.wapic.wpcmod.features.funnymap.core.map.*
import net.wapic.wpcmod.features.funnymap.dungeon.Dungeon.Info.ended
import net.wapic.wpcmod.features.funnymap.utils.MapUtils
import net.wapic.wpcmod.features.funnymap.utils.TabList
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.DungeonUtils.inDungeons
import net.wapic.wpcmod.util.Utils.equalsOneOf

object Dungeon {
	val FunnyConfig get() = WpcMod.config.funnyMap

	val dungeonTeammates = mutableMapOf<String, DungeonPlayer>()
	val espDoors = mutableListOf<Door>()
	val isMimicFloor
		get() = DungeonUtils.currentFloor.equalsOneOf(
			DungeonUtils.DungeonFloor.FLOOR_6,
			DungeonUtils.DungeonFloor.FLOOR_7,
			DungeonUtils.DungeonFloor.MASTER_MODE_FLOOR_6,
			DungeonUtils.DungeonFloor.MASTER_MODE_FLOOR_7
		)

	// TODO: Convert to 1.21.5
	private val keyGainRegex = listOf(
		Regex(".+ has obtained .+ Key!"),
		Regex("A .+ Key was picked up!")
	)
	private val keyUseRegex = listOf(
		Regex("The BLOOD DOOR has been opened!"),
		Regex(".+ opened a WITHER door!"),
	)

	fun init() {
		ClientTickEvents.START_CLIENT_TICK.register(::onTick)
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
		DungeonEvents.END.register(::onDungeonEnd)
		DungeonEvents.START.register(::onDungeonStart)
		WorldChangeEvent.BEFORE.register { _ -> reset() }
	}

	fun onTick(client: MinecraftClient) {
		if (!inDungeons) return

		if (shouldSearchMimic()) {
			MimicDetector.findMimic()?.let {
				if (FunnyConfig.scanChatInfo) ChatUtils.sendMessage("&7Mimic Room: &c$it")
				Info.mimicFound = true
			}
		}

		if (!MapUtils.calibrated) {
			MapUtils.calibrated = MapUtils.calibrateMap()
		}

		if (MapUtils.mapDataUpdated) {
			MapUpdate.updateRooms()
			MapUtils.mapDataUpdated = false
		}

		if (isMimicFloor) {
			MimicDetector.checkMimicDead()
		}

		ScoreCalculation.updateScore()

		TabList.getDungeonTabList()?.let {
			MapUpdate.updatePlayers(it)
			RunInformation.updatePuzzleCount(it)
		}

		if (DungeonScan.shouldScan) {
			DungeonScan.scan()
		}
	}

	fun onDungeonEnd() {
		ended = true
	}

	fun onDungeonStart() {
		MapUpdate.getPlayers()
		Info.startTime = System.currentTimeMillis()
	}

	fun onMessageReceived(text: Text, isActionBar: Boolean) {
		if (!inDungeons || isActionBar) return

		if (keyGainRegex.any { it.matches(text.string) }) {
			Info.keys++
		}

		if (keyUseRegex.any { it.matches(text.string) }) {
			Info.keys--
		}

		if (text.string == "Starting in 4 seconds.") {
			MapUpdate.preloadHeads()
		}
	}

	fun reset() {
		Info.reset()
		dungeonTeammates.clear()
		espDoors.clear()
		PlayerTracker.roomClears.clear()
		MapUtils.calibrated = false
		MapUtils.mapData = null
		DungeonScan.hasScanned = false
		RunInformation.reset()
	}

	private fun shouldSearchMimic() = !Info.mimicFound && !FunnyConfig.legitMode && isMimicFloor

	object Info {
		// 6 x 6 room grid, 11 x 11 with connections
		val dungeonList = Array<Tile>(121) { Unknown(0, 0) }
		val uniqueRooms = mutableSetOf<UniqueRoom>()
		var roomCount = 0
		val puzzles = mutableMapOf<Puzzle, Boolean>()

		var trapType = ""
		var witherDoors = 0
		var cryptCount = 0
		var secretCount = 0
		var mimicFound = false

		var startTime = 0L
		var ended = false
		var keys = 0
		fun reset() {
			dungeonList.fill(Unknown(0, 0))
			uniqueRooms.clear()
			roomCount = 0
			puzzles.clear()

			trapType = ""
			witherDoors = 0
			cryptCount = 0
			secretCount = 0
			mimicFound = false

			startTime = 0L
			ended = false
			keys = 0
		}
	}
}
