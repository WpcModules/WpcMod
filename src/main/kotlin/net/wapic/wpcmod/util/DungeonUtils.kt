package net.wapic.wpcmod.util

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import net.minecraft.text.Text
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.PlayerListChangeEvent
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.util.ChatUtils.removeFormatting

object DungeonUtils {
	private const val DUNGEON_START_MESSAGE: String =
		"§e[NPC] §bMort§f: Here, I found this map when I first entered the dungeon."

	private const val DUNGEON_END_MESSAGE: String = "> EXTRA STATS <"
	private val ENTERED_FLOOR_MESSAGE: Regex =
		Regex("(?<rank>\\[.{3,5}]\\s)?(?<player>\\w+) entered (?<mm>MM\\s)?The Catacombs, Floor (?<floor>.+)!")

	private val puzzleRegex = Regex(" (?<puzzle>.+): \\[(?:(?<completed>✔)|(?<failed>✖)|(?<missing>✦))] ?")
	private val incompletePuzzles: HashSet<String> = hashSetOf()
	private val failedPuzzles: HashSet<String> = hashSetOf()

	var currentFloor: DungeonFloor = DungeonFloor.NONE
		private set

	fun init() {
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
		PlayerListChangeEvent.EVENT.register(::onPlayerListChange)
		WorldChangeEvent.EVENT.register { client, world ->
			incompletePuzzles.clear()
			failedPuzzles.clear()
		}
	}

	private fun onMessageReceived(message: Text, actionBar: Boolean) {
		if (actionBar) return

		val matcher = ENTERED_FLOOR_MESSAGE.find(message.string)
		matcher?.let { result ->
			val isMasterMode = result.groups["mm"] != null
			val floor = result.groups["floor"]?.value

			when (floor) {
				"I" -> currentFloor = if (isMasterMode) DungeonFloor.MASTER_MODE_FLOOR_1 else DungeonFloor.FLOOR_1
				"II" -> currentFloor = if (isMasterMode) DungeonFloor.MASTER_MODE_FLOOR_2 else DungeonFloor.FLOOR_2
				"III" -> currentFloor = if (isMasterMode) DungeonFloor.MASTER_MODE_FLOOR_3 else DungeonFloor.FLOOR_3
				"IV" -> currentFloor = if (isMasterMode) DungeonFloor.MASTER_MODE_FLOOR_4 else DungeonFloor.FLOOR_4
				"V" -> currentFloor = if (isMasterMode) DungeonFloor.MASTER_MODE_FLOOR_5 else DungeonFloor.FLOOR_5
				"VI" -> currentFloor = if (isMasterMode) DungeonFloor.MASTER_MODE_FLOOR_6 else DungeonFloor.FLOOR_6
				"VII" -> currentFloor = if (isMasterMode) DungeonFloor.MASTER_MODE_FLOOR_7 else DungeonFloor.FLOOR_7
			}
		}

		if (message.string == DUNGEON_START_MESSAGE) {
			WpcMod.logger.debug("Dungeon Started")
			DungeonEvents.START.invoker().onStart()
			if (currentFloor == DungeonFloor.NONE) {
				WpcMod.logger.error("Current dungeon floor was not found!")
			}
		}

		if (message.string.removeFormatting().trim() == DUNGEON_END_MESSAGE) {
			WpcMod.logger.debug("Dungeon Ended")
			DungeonEvents.END.invoker().onEnd()
		}

	}

	private fun onPlayerListChange(entries: List<PlayerListS2CPacket.Entry>) {
		if (Utils.getLocation() != Island.DUNGEON) return

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
		NONE("")
	}
}