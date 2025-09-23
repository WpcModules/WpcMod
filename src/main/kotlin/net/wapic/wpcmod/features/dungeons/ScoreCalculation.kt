package net.wapic.wpcmod.features.dungeons

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.wapic.wpcmod.events.EntityEvents
import net.wapic.wpcmod.events.PlayerListChangeEvent
import net.wapic.wpcmod.events.ScoreboardChangeEvent
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.DungeonUtils.DungeonFloor
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

class ScoreCalculation {

	private val deathsTabPattern = Regex("Team Deaths: (?<deaths>\\d+)")
	private val missingPuzzlePattern = Regex("Puzzles: \\((?<count>\\d)\\)")
	private val failedPuzzlePattern = Regex(" (?<puzzle>.+): \\[✖]")
	private val solvedPuzzlePattern = Regex(" (?<puzzle>.+): \\[✔]")
	private val secretsFoundPattern = Regex(" Secrets Found: (?<secrets>\\d+)")
	private val secretsFoundPercentagePattern = Regex(" Secrets Found: (?<percentage>[\\d.]+)%")
	private val cryptsPattern = Regex(" Crypts: (?<crypts>\\d+)")
	private val dungeonClearedPattern = Regex("Cleared: (?<percentage>\\d+)% \\(\\d+\\)")
	private val timeElapsedPattern = Regex(" Time: (?:(?<hrs>\\d+)h )?(?:(?<min>\\d+)m )?(?:(?<sec>\\d+)s)?")
	private val roomCompletedPattern = Regex(" Completed Rooms: (?<count>\\d+)")

	private val skytilsMimicMessage = Regex("\\\$SKYTILS-DUNGEON-SCORE-MIMIC")
	private val mimicMessage = Regex("Mimic (Dead|Killed)(!)?")

	data class FloorRequirement(val secretPercentage: Double = 1.0, val speed: Int = 10 * 60)

	private val floorRequirements = hashMapOf(
		DungeonFloor.ENTRANCE to FloorRequirement(.3, 20 * 60),
		DungeonFloor.FLOOR_1 to FloorRequirement(.3),
		DungeonFloor.FLOOR_2 to FloorRequirement(.4),
		DungeonFloor.FLOOR_3 to FloorRequirement(.5),
		DungeonFloor.FLOOR_4 to FloorRequirement(.6, 12 * 60),
		DungeonFloor.FLOOR_5 to FloorRequirement(.7),
		DungeonFloor.FLOOR_6 to FloorRequirement(.85, 12 * 60),
		DungeonFloor.FLOOR_7 to FloorRequirement(speed = 14 * 60),
		DungeonFloor.MASTER_MODE_FLOOR_1 to FloorRequirement(speed = 8 * 60),
		DungeonFloor.MASTER_MODE_FLOOR_2 to FloorRequirement(speed = 8 * 60),
		DungeonFloor.MASTER_MODE_FLOOR_3 to FloorRequirement(speed = 8 * 60),
		DungeonFloor.MASTER_MODE_FLOOR_4 to FloorRequirement(speed = 8 * 60),
		DungeonFloor.MASTER_MODE_FLOOR_5 to FloorRequirement(speed = 8 * 60),
		DungeonFloor.MASTER_MODE_FLOOR_6 to FloorRequirement(speed = 8 * 60),
		DungeonFloor.MASTER_MODE_FLOOR_7 to FloorRequirement(speed = 15 * 60),
		DungeonFloor.NONE to FloorRequirement()
	)

	private val floorRequirement get() = floorRequirements[DungeonUtils.currentFloor]!!
	private val isEntrance get() = DungeonUtils.currentFloor == DungeonFloor.ENTRANCE

	// Room Clear
	private var completedRooms = 0
	private var clearedPercentage = 0

	private val totalRoomMap = mutableMapOf<Int, Int>()
	private val totalRooms: Int
		get() {
			val a = if (clearedPercentage > 0 && completedRooms > 0) {
				(100 * (completedRooms / clearedPercentage.toDouble())).roundToInt()
			} else 0
			if (a == 0) return 0
			totalRoomMap[a] = (totalRoomMap[a] ?: 0) + 1
			return totalRoomMap.toList().maxByOrNull { it.second }!!.first
		}

	private val roomClearPercentage
		get() = if (totalRooms > 0) (completedRooms / totalRooms.toDouble()).coerceAtMost(
			1.0
		) else 0.0
	val roomClearScore get() = applyEntranceModifier((60.0 * roomClearPercentage).coerceIn(0.0, 60.0))

	// Secrets
	private var foundSecrets = 0
	private var totalSecrets = 0

	private val secretsNeeded: Int
		get() {
			if (totalSecrets == 0) return 1
			return ceil(totalSecrets * floorRequirement.secretPercentage).toInt()
		}
	private val secretsClearedPercentage get() = foundSecrets / secretsNeeded.toDouble()
	private val secretScore
		get() = if (totalSecrets <= 0) 0 else applyEntranceModifier(
			(40f * secretsClearedPercentage).coerceIn(
				0.0,
				40.0
			)
		)

	private val exploreScore get() = roomClearScore + secretScore

	// Death
	private var deaths = 0
	private var firstDeathHadSpirit = false
	private val deathPenalty get() = (2 * deaths) - if (firstDeathHadSpirit) 1 else 0

	// Puzzle

	private var missingPuzzles = 0
	private var failedPuzzles = 0
	private val puzzlePenalty get() = 10 * (missingPuzzles + failedPuzzles)

	private val skillScore
		get() = applyEntranceModifier(20 + (80.0 * roomClearPercentage) - puzzlePenalty - deathPenalty).coerceIn(
			20,
			100
		)

	// Speed
	private var secondsElapsed = 0.0
	private val totalElapsed get() = secondsElapsed + 480 - (floorRequirement.speed)

	private val speedScore
		get() = when {
			totalElapsed < 492.0 -> 100.0
			totalElapsed < 600.0 -> 140 - totalElapsed / 12.0
			totalElapsed < 840.0 -> 115 - totalElapsed / 24.0
			totalElapsed < 1140.0 -> 108 - totalElapsed / 30.0
			totalElapsed < 3570.0 -> 98.5 - totalElapsed / 40.0
			else -> 0.0
		}.let { applyEntranceModifier(it) }

	// Bonus
	private var mimicFound = false
	private var isPaul = false
	private var princeKilled = false
	private var crypts = 0

	private val calcBonusScore
		get() = crypts.coerceIn(
			0,
			5
		) + isPaul.ifTrue(10) + mimicFound.ifTrue(2) + princeKilled.ifTrue(1)
	private val bonusScore get() = if (isEntrance) ceil(calcBonusScore * 0.7).toInt() else calcBonusScore

	private val totalScore get() = skillScore + exploreScore + speedScore + bonusScore

	private val rank
		get() = when {
			totalScore < 100 -> "§cD"
			totalScore < 160 -> "§9C"
			totalScore < 230 -> "§aB"
			totalScore < 270 -> "§5A"
			totalScore < 300 -> "§eS"
			else -> "§6§lS+"
		}

	init {
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
		WorldChangeEvent.EVENT.register(::onWorldChange)
		PlayerListChangeEvent.EVENT.register(::onPlayerListChange)
		ScoreboardChangeEvent.EVENT.register(::onScoreboardChange)
		EntityEvents.DESPAWN.register(::onEntityDespawn)
		DungeonEvents.PUZZLE_RESET.register(::onPuzzleReset)

		HudLayerRegistrationCallback.EVENT.register { layeredDrawer ->
			layeredDrawer.attachLayerBefore(
				IdentifiedLayer.CHAT,
				IdentifiedLayer.of(Identifier.of("wpcmod", "score_calculation"), ::onRenderHud)
			)
		}
	}

	private fun Boolean.ifTrue(num: Int) = if (this) num else 0
	private fun applyEntranceModifier(value: Double) = if (isEntrance) (value * 0.7).toInt() else value.toInt()

	private fun onWorldChange(client: MinecraftClient, world: ClientWorld) {
		completedRooms = 0
		clearedPercentage = 0
		totalRoomMap.clear()

		foundSecrets = 0
		totalSecrets = 0

		deaths = 0
		firstDeathHadSpirit = false

		missingPuzzles = 0
		failedPuzzles = 0

		secondsElapsed = 0.0

		mimicFound = false
		isPaul = false
		princeKilled = false
		crypts = 0
	}

	private fun onPuzzleReset() {
		missingPuzzles = (missingPuzzles + 1)
		failedPuzzles = (failedPuzzles - 1).coerceAtLeast(0)
	}

	private fun onEntityDespawn(entity: Entity) {
		if (entity is ZombieEntity && entity.isBaby) {
			mimicFound = true
			Utils.runCommand("/pc Mimic Killed!")
		}
	}

	private fun onScoreboardChange(line: String) {
		if (Utils.getLocation() != Island.DUNGEON) return

		if (line.startsWith("Cleared: ")) {
			val matcher = dungeonClearedPattern.find(line)
			if (matcher != null) {
				clearedPercentage = matcher.groups["percentage"]?.value?.toIntOrNull() ?: 0
				return
			}
		}
	}

	private fun onPlayerListChange(entries: List<PlayerListS2CPacket.Entry>) {
		if (Utils.getLocation() != Island.DUNGEON) return

		entries.forEach { playerData ->
			val name = playerData.displayName?.string ?: playerData.profile?.name ?: return@forEach

			when {
				name.contains("Deaths: ") -> {
					val matcher = deathsTabPattern.find(name) ?: return@forEach
					deaths = matcher.groups["deaths"]?.value?.toIntOrNull() ?: 0
				}

				name.contains("Puzzles:") -> {
					val matcher = missingPuzzlePattern.find(name) ?: return@forEach
					missingPuzzles = matcher.groups["count"]?.value?.toIntOrNull() ?: 0
				}

				name.contains("✔") -> {
					if (solvedPuzzlePattern.containsMatchIn(name)) {
						missingPuzzles = (missingPuzzles - 1).coerceAtLeast(0)
					}
				}

				name.contains("✖") -> {
					if (failedPuzzlePattern.containsMatchIn(name)) {
						missingPuzzles = (missingPuzzles - 1).coerceAtLeast(0)
						failedPuzzles = (failedPuzzles + 1)
					}
				}

				name.contains("Secrets Found:") -> {
					if (name.contains("%")) {
						val matcher = secretsFoundPercentagePattern.find(name) ?: return@forEach
						val percentagePer = matcher.groups["percentage"]?.value?.toDoubleOrNull() ?: 0.0
						totalSecrets =
							if (foundSecrets > 0 && percentagePer > 0) floor(100f / percentagePer * foundSecrets + 0.5).toInt() else 0
					} else {
						val matcher = secretsFoundPattern.find(name) ?: return@forEach
						foundSecrets = matcher.groups["secrets"]?.value?.toIntOrNull() ?: 0
					}
				}

				name.contains("Crypts:") -> {
					val matcher = cryptsPattern.find(name) ?: return@forEach
					crypts = matcher.groups["crypts"]?.value?.toIntOrNull() ?: 0
				}

				name.contains("Completed Rooms") -> {
					val matcher = roomCompletedPattern.find(name) ?: return@forEach
					completedRooms = matcher.groups["count"]?.value?.toIntOrNull() ?: return@forEach
				}

				name.contains("Time:") -> {
					val matcher = timeElapsedPattern.find(name) ?: return@forEach

					val hours = matcher.groups["hrs"]?.value?.toIntOrNull() ?: 0
					val minutes = matcher.groups["min"]?.value?.toIntOrNull() ?: 0
					val seconds = matcher.groups["sec"]?.value?.toIntOrNull() ?: 0
					secondsElapsed = ((hours * 3600) + (minutes * 60) + seconds).toDouble()
				}
			}
		}
	}

	private fun onMessageReceived(text: Text, actionBar: Boolean) {
		if (actionBar || Utils.getLocation() != Island.DUNGEON) return
		val message = text.string

		if (message == "A Prince falls. +1 Bonus Score") {
			princeKilled = true
		}

		if (message.startsWith("Party >")) {
			if (message.contains(skytilsMimicMessage) || message.contains(mimicMessage)) {
				mimicFound = true
			}
		}
	}

	private fun onRenderHud(drawContext: DrawContext, tickCounter: RenderTickCounter) {
		if (Utils.getLocation() != Island.DUNGEON) return
		val tr = MinecraftClient.getInstance().textRenderer

		drawContext.drawText(tr, "§9Dungeon Status", 2, 48, 0xffffff, true)
		drawContext.drawText(tr, "§f* §eDeaths: §c$deaths", 2, 58, 0xffffff, true)
		drawContext.drawText(tr, "§f* §eMissing Puzzles: §c$missingPuzzles", 2, 68, 0xffffff, true)
		drawContext.drawText(tr, "§f* §eFailed Puzzles: §c$failedPuzzles", 2, 78, 0xffffff, true)
		drawContext.drawText(
			tr,
			"§f* §eSecrets: §a$foundSecrets§7/§a$secretsNeeded §7(§6Total: $totalSecrets§7)",
			2,
			88,
			0xffffff,
			true
		)
		drawContext.drawText(tr, "§f* §eCrypts: §a$crypts", 2, 98, 0xffffff, true)
		drawContext.drawText(tr, "§f* §eMimic: ${if (mimicFound) "§a✔" else "§c✖"}", 2, 108, 0xffffff, true)
		drawContext.drawText(tr, "§f* §ePrince: ${if (princeKilled) "§a✔" else "§c✖"}", 2, 118, 0xffffff, true)

		drawContext.drawText(tr, "§9Score", 2, 128, 0xffffff, true)
		drawContext.drawText(tr, "§f* §eSkill Score:§a $skillScore", 2, 138, 0xffffff, true)
		drawContext.drawText(
			tr,
			"§f* §eExplore Score:§a $exploreScore §7(§e$roomClearScore §7+ §6$secretScore§7)",
			2,
			148,
			0xffffff,
			true
		)
		drawContext.drawText(tr, "§f* §eSpeed Score:§a $speedScore", 2, 158, 0xffffff, true)
		drawContext.drawText(tr, "§f* §eBonus Score:§a $bonusScore", 2, 168, 0xffffff, true)
		drawContext.drawText(tr, "§f* §eTotal Score:§a $totalScore", 2, 178, 0xffffff, true)
		drawContext.drawText(tr, "§f* §eRank: $rank", 2, 188, 0xffffff, true)
	}
}