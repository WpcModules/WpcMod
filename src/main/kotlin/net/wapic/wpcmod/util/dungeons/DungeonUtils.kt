package net.wapic.wpcmod.util.dungeons

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.util.Util
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.PlayerListChangeEvent
import net.wapic.wpcmod.events.ScoreboardChangeEvent
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.ScoreCalculation
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.Room
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.RoomState
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.UniqueRoom
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.DungeonScan
import net.wapic.wpcmod.features.dungeons.funnymap.utils.MapUtils
import net.wapic.wpcmod.util.*
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

	val inDungeons get() = Utils.getLocation() == Island.DUNGEON
	var currentFloor: DungeonFloor = DungeonFloor.NONE
		private set
	var currentRoom: UniqueRoom? = null
		private set

	var startTime = 0L
		private set
	val dungeonTeammates = mutableMapOf<String, DungeonPlayer>()
	var bossSpawned = false
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
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)

		WorldChangeEvent.BEFORE.register { _ ->
			incompletePuzzles.clear()
			failedPuzzles.clear()
			bossSpawned = false
			currentFloor = DungeonFloor.NONE
			startTime = 0L
			dungeonTeammates.clear()
		}
	}

	private fun onTick(client: Minecraft) {
		if (!inDungeons) return
		TabListUtil.getDungeonTabList()?.let(::updatePlayers)
	}

	fun preloadPlayerHeads(tabEntries: List<Pair<PlayerInfo, Component>>) {
		for (i in listOf(5, 9, 13, 17, 1)) tabEntries[i].first.skin
	}

	fun getPlayers(tabEntries: List<Pair<PlayerInfo, Component>>) {
		for (i in listOf(5, 9, 13, 17, 1)) {
			with(tabEntries[i]) {
				val tabText = second.string.trim()
				val name = tabText.substringAfterLast("] ").split(" ")[0]
				if (name != "") {
					dungeonTeammates[name] = DungeonPlayer(first.skin).apply {
						MC.level?.players()?.find { it.name.string == name }?.let { setData(it) }
						this.name = name
						this.dungeonClass = DungeonClass.fromTabText(tabText.substringAfter("(").substringBefore(")").substringBefore(" "))
					}
				}
			}
		}
	}

	fun updatePlayers(tabEntries: List<Pair<PlayerInfo, Component>>) {
		if (dungeonTeammates.isEmpty()) return
		val time = Util.getMillis() - startTime

		for ((index, value) in listOf(5, 9, 13, 17, 1).withIndex()) {
			val tabText = tabEntries[value].second.string.trim()
			val name = tabText.substringAfterLast("] ").split(" ")[0]
			if (name.isEmpty()) continue

			dungeonTeammates[name]?.run {
				dead = tabText.contains("(DEAD)")
				if (dead) continue

				if (dungeonClass == DungeonClass.EMPTY) {
					val classText = tabText.substringAfter("(").substringBefore(")").substringBefore(" ")
					dungeonClass = DungeonClass.fromTabText(classText)
				}

				val player = MC.level?.players()?.find { it.stringUUID == uuid }?.let {
					if (!playerLoaded) setData(it)
					this.updatePos(
						((it.x - DungeonScan.START_X + 13) * MapUtils.coordMultiplier + MapUtils.startCorner.first).toFloat(),
						((it.z - DungeonScan.START_Z + 13) * MapUtils.coordMultiplier + MapUtils.startCorner.second).toFloat(),
						it.yRot
					)
					return@let it
				}

				if (player == null) {
					MapUtils.mapData?.decorations?.elementAtOrNull(index)?.let { decoration ->
						if (decoration.type == MapDecorationTypes.FRAME) return@let // no need to update local player from map
						this.updatePos(
							((decoration.x + 128) shr 1).toFloat(),
							((decoration.y + 128) shr 1).toFloat(),
							decoration.rot * 22.5f
						)
					}
				}

				val room = getCurrentRoom()
				room?.let { current ->
					if (time <= 1000) return@let
					if (lastRoom == null) {
						lastRoom = current
					} else if (lastRoom?.data?.name != current.data.name) {

						if (current.state.equalsOneOf(RoomState.UNDISCOVERED, RoomState.UNOPENED)) {
							current.uniqueRoom?.setRoomState(RoomState.DISCOVERED)
						}

						lastRoom?.let { last ->
							if (isPlayer) {
								DungeonEvents.ROOM_ENTERED.invoker().onRoomEntered(last, current)
							}
							roomVisits.add(Pair(time - lastTime, last))
							lastTime = time
							lastRoom = current
						}
					}
				}
			}
		}
	}

	private fun onMessageReceived(message: Component, actionBar: Boolean) {
		if (actionBar || !inDungeons) return

		if (message.string.removeFormatting() == "Starting in 4 seconds.") {
			TabListUtil.getDungeonTabList()?.let(::preloadPlayerHeads)
		}

		if (message.string == DUNGEON_START_MESSAGE) {
			WpcMod.LOGGER.debug("Dungeon Started")
			TabListUtil.getDungeonTabList()?.let(::getPlayers)
			startTime = Util.getMillis()
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
			if (!bossSpawned && bossName != "The Watcher" && currentFloor != DungeonFloor.NONE && checkBossName(currentFloor, bossName)) {
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

	private fun onRoomEntered(oldRoom: Room, newRoom: Room) {
		currentRoom = newRoom.uniqueRoom
		WpcMod.LOGGER.debug("Current room set: {}, old room: {}", newRoom.data.name, oldRoom.data.name)
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
		if (!currentFloor.equalsOneOf(DungeonFloor.FLOOR_7, DungeonFloor.MASTER_MODE_FLOOR_7) || !bossSpawned) return F7Phase.UNKNOWN

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
				return entries.find { it.stateName == stateName } ?: UNKNOWN
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

	enum class DungeonClass(val color: Int) {
		ARCHER(11141120),
		BERSERK(16755200),
		HEALER(16733695),
		MAGE(5636095),
		TANK(43520),
		EMPTY(0);

		companion object {

			fun fromTabText(text: String): DungeonClass {
				return entries.find { it.name.equals(text, ignoreCase = true) } ?: EMPTY
			}
		}
	}
}