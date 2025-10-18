package net.wapic.wpcmod.features.dungeons

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.dungeon.DungeonConfig.ScoreCalculationConfig.ScoreHudType
import net.wapic.wpcmod.events.*
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.FunnyMap
import net.wapic.wpcmod.jarvis.SimpleHudElement
import net.wapic.wpcmod.util.APIUtils
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.DungeonUtils.DungeonFloor
import net.wapic.wpcmod.util.DungeonUtils.isMimicFloor
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

object ScoreCalculation : SimpleHudElement("Score Calculation", 140, 160) {

	private val config get() = WpcMod.config.dungeon.scoreCalculation

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
	private val princeMessage = Regex("Prince (Dead|Killed)(!)?")

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
		DungeonFloor.MASTER_MODE_FLOOR_6 to FloorRequirement(),
		DungeonFloor.MASTER_MODE_FLOOR_7 to FloorRequirement(speed = 15 * 60),
	)

	private val floorRequirement get() = floorRequirements[DungeonUtils.currentFloor] ?: FloorRequirement()
	private val isEntrance get() = DungeonUtils.currentFloor == DungeonFloor.ENTRANCE

	// Room Clear
	private var completedRooms = 0
	private var clearedPercentage = 0
	private val totalRoomMap = mutableMapOf<Int, Int>()
	var bloodCleared = false

	private val roomClearPercentage: Double
		get() {
			val total = if(FunnyMap.Info.roomCount != 0) FunnyMap.Info.roomCount else getTotalRooms()
			val complete = completedRooms + (!DungeonUtils.bossSpawned).ifTrue(1) + (!bloodCleared).ifTrue(1)
			return if (total > 0) (complete / total.toDouble()).coerceAtMost(1.0) else 0.0
		}

	private val roomClearScore get() = (60 * roomClearPercentage).coerceIn(0.0, 60.0).applyEntranceModifier()

	// Secrets
	private var foundSecrets = 0
	var totalSecrets = 0

	private val secretsNeeded get() = if (totalSecrets == 0) 1 else ceil(totalSecrets * floorRequirement.secretPercentage).toInt()
	private val secretsClearedPercentage get() = foundSecrets / secretsNeeded.toDouble()
	private val secretScore: Int
		get() {
			if (totalSecrets <= 0) return 0
			val score = (40f * secretsClearedPercentage).coerceIn(0.0, 40.0)
			return score.applyEntranceModifier()
		}

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
		get() = (20.0 + roomClearPercentage * 80.0 - puzzlePenalty - deathPenalty).applyEntranceModifier()
			.coerceIn(20, 100)

	// Speed
	private var secondsElapsed = 0.0
	private val totalElapsed get() = secondsElapsed + 480 - (floorRequirement.speed)

	private val speedScore: Int
		get() {
			val score = when {
				totalElapsed < 492.0 -> 100.0
				totalElapsed < 600.0 -> 140 - totalElapsed / 12.0
				totalElapsed < 840.0 -> 115 - totalElapsed / 24.0
				totalElapsed < 1140.0 -> 108 - totalElapsed / 30.0
				totalElapsed < 3570.0 -> 98.5 - totalElapsed / 40.0
				else -> 0.0
			}
			return score.applyEntranceModifier()
		}

	// Bonus
	var mimicFound = false
	private var isPaul = false
		get() = if(config.assumePaul) true else field
	private var princeKilled = false
	private var crypts = 0

	private val calcBonusScore
		get() = crypts.coerceAtMost(5) + isPaul.ifTrue(10) + mimicFound.ifTrue(2) + princeKilled.ifTrue(1)
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

	var mimicOpenTime = 0L
	var mimicPos: BlockPos? = null

	fun init() {
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
		WorldChangeEvent.BEFORE.register(::onWorldChange)
		PlayerListChangeEvent.EVENT.register(::onPlayerListChange)
		ScoreboardChangeEvent.EVENT.register(::onScoreboardChange)
		EntityEvents.DESPAWN.register(::onEntityDespawn)
		DungeonEvents.PUZZLE_RESET.register(::onPuzzleReset)
		BlockEvents.CHANGE.register(::onBlockChange)
		DungeonEvents.START.register { isPaul = APIUtils.hasBonusPaulScore() }
		ClientTickEvents.START_CLIENT_TICK.register { if(isMimicFloor) checkMimicDead() }
	}

	private fun Boolean.ifTrue(num: Int) = if (this) num else 0
	private fun Double.applyEntranceModifier() = if (isEntrance) (this * 0.7).toInt() else this.toInt()
	private fun getTotalRooms(): Int {
		if (clearedPercentage > 0 && completedRooms > 0) {
			val key = (100 * (completedRooms / clearedPercentage.toDouble())).roundToInt()
			totalRoomMap[key] = (totalRoomMap[key] ?: 0) + 1
			return totalRoomMap.toList().maxByOrNull { it.second }!!.first
		}
		return 0
	}

	private fun onWorldChange(world: ClientWorld) {
		bloodCleared = false
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

	fun checkMimicDead() {
		if (mimicOpenTime == 0L || mimicFound) return
		if (System.currentTimeMillis() - mimicOpenTime < 750) return

		val playerDistanceFromMimic = MC.player?.squaredDistanceTo(mimicPos?.toCenterPos()) ?: return

		if (playerDistanceFromMimic < 400.0) {
			val isMimicDead = MC.world?.entities?.none { it is ZombieEntity && it.isBaby }
			if (isMimicDead == true)
				mimicFound = true
		}
	}

	fun onBlockChange(pos: BlockPos, old: BlockState, new: BlockState) {
		if (old.block == Blocks.TRAPPED_CHEST && new.block == Blocks.AIR) {
			mimicOpenTime = System.currentTimeMillis()
			mimicPos = pos
		}
	}

	private fun onPuzzleReset() {
		if(!isActive) return
		missingPuzzles = (missingPuzzles + 1)
		failedPuzzles = (failedPuzzles - 1).coerceAtLeast(0)
	}

	private fun onEntityDespawn(entity: Entity) {
		if (!isActive) return
		if (entity is ZombieEntity && entity.isBaby) {
			mimicFound = true
			if (config.mimicMessage) Utils.runCommand("/pc Mimic Killed!")
		}
	}

	private fun onScoreboardChange(line: String) {
		if(!isActive) return

		if (line.startsWith("Cleared: ")) {
			val matcher = dungeonClearedPattern.find(line)
			if (matcher != null) {
				clearedPercentage = matcher.groups["percentage"]?.value?.toIntOrNull() ?: 0
				return
			}
		}
	}

	private fun onPlayerListChange(entries: List<PlayerListS2CPacket.Entry>) {
		if(!isActive) return

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
		if (!isActive || actionBar) return
		val message = text.string

		if (message == "A Prince falls. +1 Bonus Score") {
			princeKilled = true
			Utils.runCommand("/pc Prince Killed!")
		}

		if (message.startsWith("Party >")) {
			if (message.contains(skytilsMimicMessage) || message.contains(mimicMessage)) {
				mimicFound = true
			}

			if (message.contains(princeMessage)) {
				princeKilled = true
			}
		}
	}


	override fun render(drawContext: DrawContext, renderTickCounter: RenderTickCounter) {
		if (!isActive) return
		val tr = MinecraftClient.getInstance().textRenderer

		drawContext.matrices.push()
		applyTransformations(drawContext.matrices)

		val minimizedLine = setOf<Text>(Text.literal("§eScore: §a$totalScore §7($rank§7)"))

		var statusLines = setOf<Text>(
			Text.literal("§9Dungeon Status"),
			Text.literal("§f* §eDeaths: §c$deaths"),
			Text.literal("§f* §eMissing Puzzles: §c$missingPuzzles"),
			Text.literal("§f* §eFailed Puzzles: §c$failedPuzzles"),
			Text.literal("§f* §eSecrets: §a$foundSecrets§7/§a$secretsNeeded §7(§6Total: $totalSecrets§7)"),
			Text.literal("§f* §eCrypts: §a$crypts"),
			Text.literal("§f* §ePrince: ${if(princeKilled) "§a✔" else "§c✖"}"),
		)

		val mimicLine = Text.literal("§f* §eMimic: ${if (mimicFound) "§a✔" else "§c✖"}")

		if(isMimicFloor) statusLines = statusLines + mimicLine

		val scoreLines = setOf<Text>(
			Text.literal(""),
			Text.literal("§9Score"),
			Text.literal("§f* §eSkill Score: §a$skillScore"),
			Text.literal("§f* §eExplore Score: §a$exploreScore §7(§e$roomClearScore §7+ §6$secretScore§7)"),
			Text.literal("§f* §eSpeed Score: §a$speedScore"),
			Text.literal("§f* §eBonus Score: §a$bonusScore"),
			Text.literal("§f* §eTotal Score: §a$totalScore"),
			Text.literal("§f* §eRank: $rank")
		)

		val lines = if(config.scoreEstimate == ScoreHudType.MINIMIZED) minimizedLine else statusLines + scoreLines

		for ((index, line) in lines.withIndex()) {
			drawContext.drawText(tr, line, x.toInt(), y.toInt() + (index * 10), 0xffffff, true)
		}

		drawContext.matrices.pop()
	}

	override fun isEnabled(): Boolean {
		return config.scoreEstimate != ScoreHudType.DISABLED
	}

	override fun isActive(): Boolean {
		return config.scoreEstimate != ScoreHudType.DISABLED && DungeonUtils.inDungeons
	}
}