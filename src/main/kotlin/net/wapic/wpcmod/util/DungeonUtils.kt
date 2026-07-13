package net.wapic.wpcmod.util

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.PlayerListChangeEvent
import net.wapic.wpcmod.events.ScoreboardChangeEvent
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.ScoreCalculation
import net.wapic.wpcmod.util.ChatUtils.removeFormatting
import net.wapic.wpcmod.util.Utils.equalsOneOf

object DungeonUtils {
	private const val DUNGEON_START_MESSAGE: String =
		"§e[NPC] §bMort§f: Here, I found this map when I first entered the dungeon."

	private const val DUNGEON_END_MESSAGE: String = "> EXTRA STATS <"

	private val puzzleRegex = Regex(" (?<puzzle>.+): \\[(?:(?<completed>✔)|(?<failed>✖)|(?<missing>✦))] ?")
	private val floorRegex = Regex("^ \uE067 The Catacombs \\((?<floor>[FME][1-7]?)\\)$")
	private val incompletePuzzles: HashSet<String> = hashSetOf()
	private val failedPuzzles: HashSet<String> = hashSetOf()
	var bossSpawned = false
		private set
	var currentRoom: String = ""
		private set

	val inDungeons get() = Utils.getLocation() == Island.DUNGEON

	var currentFloor: DungeonFloor = DungeonFloor.NONE
		private set

	val isMimicFloor: Boolean
		get() = currentFloor.equalsOneOf(
			DungeonFloor.FLOOR_6,
			DungeonFloor.FLOOR_7,
			DungeonFloor.MASTER_MODE_FLOOR_6,
			DungeonFloor.MASTER_MODE_FLOOR_7
		)

	fun init() {
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
		PlayerListChangeEvent.EVENT.register(::onPlayerListChange)
		ScoreboardChangeEvent.EVENT.register(::onScoreboardUpdate)
		DungeonEvents.ROOM_ENTERED.register(::onRoomEntered)

		WorldChangeEvent.BEFORE.register { _ ->
			incompletePuzzles.clear()
			failedPuzzles.clear()
			bossSpawned = false
			currentFloor = DungeonFloor.NONE
		}
	}

	private fun onMessageReceived(message: Component, actionBar: Boolean) {
		if (actionBar || !inDungeons) return

		if (message.string == DUNGEON_START_MESSAGE) {
			WpcMod.LOGGER.debug("Dungeon Started")
			DungeonEvents.START.invoker().onStart()
			if (currentFloor == DungeonFloor.NONE) {
				WpcMod.LOGGER.warn("Current dungeon floor was not found!")
				ChatUtils.sendMessage("Current dungeon floor could not be detected, some features may not work properly")
			}
		}

		if (message.string.removeFormatting().trim() == DUNGEON_END_MESSAGE) {
			WpcMod.LOGGER.debug("Dungeon Ended")
			DungeonEvents.END.invoker().onEnd()
		}

		if (message.string.startsWith("[BOSS]") && message.string.contains(":")) {
			val bossName = message.string.substringAfter("[BOSS] ").substringBefore(":").trim()
			if (!bossSpawned && bossName != "The Watcher" && currentFloor != DungeonFloor.NONE && checkBossName(
					currentFloor,
					bossName
				)
			) {
				bossSpawned = true
				ScoreCalculation.bloodCleared = true
			}
		}
	}

	private fun onScoreboardUpdate(line: String) {
		if (!inDungeons) return

		if (line.contains("The Catacombs (")) {
			val matcher = floorRegex.find(line)
			currentFloor = DungeonFloor.fromShortName(matcher?.groups["floor"]?.value)
		}
	}

	private fun onRoomEntered(oldRoom: String, newRoom: String) {
		currentRoom = newRoom
		WpcMod.LOGGER.debug("Current room set: {}, old room: {}", newRoom, oldRoom)
	}

	private fun checkBossName(floor: DungeonFloor, bossName: String): Boolean {
		val correctBoss = when (floor) {
			DungeonFloor.ENTRANCE -> "The Watcher"
			DungeonFloor.FLOOR_1, DungeonFloor.MASTER_MODE_FLOOR_1 -> "Bonzo"
			DungeonFloor.FLOOR_2, DungeonFloor.MASTER_MODE_FLOOR_2 -> "Scarf"
			DungeonFloor.FLOOR_3, DungeonFloor.MASTER_MODE_FLOOR_3 -> "The Professor"
			DungeonFloor.FLOOR_4, DungeonFloor.MASTER_MODE_FLOOR_4 -> "Thorn"
			DungeonFloor.FLOOR_5, DungeonFloor.MASTER_MODE_FLOOR_5 -> "Livid"
			DungeonFloor.FLOOR_6, DungeonFloor.MASTER_MODE_FLOOR_6 -> "Sadan"
			DungeonFloor.FLOOR_7, DungeonFloor.MASTER_MODE_FLOOR_7 -> "Maxor"
			else -> null
		} ?: return false
		return bossName.endsWith(correctBoss)
	}

	private fun onPlayerListChange(entries: List<ClientboundPlayerInfoUpdatePacket.Entry>) {
		if (!inDungeons) return

		entries.forEach { playerData ->
			val name = playerData.displayName?.string ?: playerData.profile?.name ?: return@forEach

			puzzleRegex.find(name)?.let { result ->
				val puzzleName = result.groups["puzzle"]?.value ?: return@let
				if (puzzleName == "???") return@let

				when {
					result.groups["missing"] != null -> {
						if (puzzleName in failedPuzzles) {
							//Puzzle Reset
							DungeonEvents.PUZZLE_RESET.invoker().onPuzzleReset()
							failedPuzzles.remove(puzzleName)
							incompletePuzzles.add(puzzleName)
						}

						if (puzzleName !in incompletePuzzles) {
							//Puzzle Discovered
							incompletePuzzles.add(puzzleName)
						}
					}

					result.groups["completed"] != null || result.groups["failed"] != null -> {
						if (puzzleName in incompletePuzzles) {
							//Puzzle completed or failed
							failedPuzzles.add(puzzleName)
							incompletePuzzles.add(puzzleName)
						}
					}
				}
			}
		}
	}

	fun getF7Phase(): F7Phase {
		if (!inDungeons) return F7Phase.UNKNOWN
		if (!currentFloor.equalsOneOf(
				DungeonFloor.FLOOR_7,
				DungeonFloor.MASTER_MODE_FLOOR_7
			) || !bossSpawned
		) return F7Phase.UNKNOWN

		with(MC.player ?: return F7Phase.UNKNOWN) {
			return when {
				y > 210 -> F7Phase.MAXOR
				y > 155 -> F7Phase.STORM
				y > 100 -> F7Phase.GOLDOR
				y > 45 -> F7Phase.NECRON
				else -> F7Phase.WITHER_KING
			}
		}
	}

	enum class F7Phase(val stateName: String) {
		MAXOR("P1"),
		STORM("P2"),
		GOLDOR("P3"),
		NECRON("P4"),
		WITHER_KING("P5"),
		UNKNOWN("UNKNOWN");

		companion object {
			fun fromStateName(stateName: String): F7Phase {
				return entries.find { it.stateName == stateName} ?: UNKNOWN
			}
		}
	}

	enum class DungeonFloor(val shortName: String) {
		ENTRANCE("E"),
		FLOOR_1("F1"),
		FLOOR_2("F2"),
		FLOOR_3("F3"),
		FLOOR_4("F4"),
		FLOOR_5("F5"),
		FLOOR_6("F6"),
		FLOOR_7("F7"),
		MASTER_MODE_FLOOR_1("M1"),
		MASTER_MODE_FLOOR_2("M2"),
		MASTER_MODE_FLOOR_3("M3"),
		MASTER_MODE_FLOOR_4("M4"),
		MASTER_MODE_FLOOR_5("M5"),
		MASTER_MODE_FLOOR_6("M6"),
		MASTER_MODE_FLOOR_7("M7"),
		NONE("");

		companion object {
			fun fromShortName(shortName: String?): DungeonFloor {
				return entries.find { it.shortName == shortName } ?: NONE
			}
		}
	}
}