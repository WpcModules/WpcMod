package net.wapic.wpcmod.features.dungeons

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.ChatFormatting
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.CommonColors
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.monster.Zombie
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.dungeon.ScoreCalculationConfig.ScoreHudType
import net.wapic.wpcmod.config.dungeon.ScoreCalculationConfig.ScoreMessageType
import net.wapic.wpcmod.events.*
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.FunnyMap
import net.wapic.wpcmod.hud.SimpleHudElement
import net.wapic.wpcmod.util.*
import net.wapic.wpcmod.util.DungeonUtils.DungeonFloor
import net.wapic.wpcmod.util.DungeonUtils.isMimicFloor
import net.wapic.wpcmod.util.EntityUtils.headTexture
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.util.render.drawText
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

object ScoreCalculation : SimpleHudElement("Score Calculation", 140, 160) {

	private val config get() = WpcMod.config.dungeon.scoreCalculation
	override val isEnabled: Boolean get() = config.enabled
	override val isActive: Boolean get() = isEnabled && DungeonUtils.inDungeons

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
	private val mimicMessage = Regex("Mimic (Dead|Killed)!?", RegexOption.IGNORE_CASE)
	private val princeMessage = Regex("Prince (Dead|Killed)!?", RegexOption.IGNORE_CASE)

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
			val total = getTotalRooms()
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
		get() = if (config.assumeSpirit) true else field

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
	var mimicKilled = false
	private var isPaul = false
		get() = if(config.assumePaul) true else field
	private var princeKilled = false
	private var crypts = 0

	private val calcBonusScore
		get() = crypts.coerceAtMost(5) + isPaul.ifTrue(10) + mimicKilled.ifTrue(2) + princeKilled.ifTrue(1)
	private val bonusScore get() = if (isEntrance) ceil(calcBonusScore * 0.7).toInt() else calcBonusScore

	private var sent300Message = false
	private var sent270Message = false
	private var sentMimicMessage = false

	private val totalScore: Int get() = skillScore + exploreScore + speedScore + bonusScore

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
		PlayerListChangeEvent.EVENT.register(::onPlayerListChange)
		ScoreboardChangeEvent.EVENT.register(::onScoreboardChange)
		EntityEvents.DESPAWN.register(::onEntityDespawn)
		DungeonEvents.PUZZLE_RESET.register(::onPuzzleReset)
		BlockEvents.CHANGE.register(::onBlockChange)
		ClientTickEvents.START_CLIENT_TICK.register(::onTick)
		DungeonEvents.START.register { isPaul = APIUtils.hasBonusPaulScore() }
		WorldChangeEvent.BEFORE.register { onWorldChange() }
	}

	private fun Boolean.ifTrue(num: Int) = if (this) num else 0
	private fun Double.applyEntranceModifier() = if (isEntrance) (this * 0.7).toInt() else this.toInt()

	private fun getTotalRooms(): Int {
		if (FunnyMap.Info.roomCount != 0) return FunnyMap.Info.roomCount

		if (clearedPercentage > 0 && completedRooms > 0) {
			val key = (100 * (completedRooms / clearedPercentage.toDouble())).roundToInt()
			totalRoomMap[key] = (totalRoomMap[key] ?: 0) + 1
			return totalRoomMap.toList().maxByOrNull { it.second }!!.first
		}

		return 0
	}

	private fun sendScoreMessage(score: Int, messageType: ScoreMessageType) {
		val shouldSendMessage = messageType.equalsOneOf(ScoreMessageType.MESSAGE_AND_TITLE, ScoreMessageType.MESSAGE)
		val shouldSendTitle = messageType.equalsOneOf(ScoreMessageType.MESSAGE_AND_TITLE, ScoreMessageType.TITLE)

		if (shouldSendMessage) {
			Utils.runCommand("/pc $score Score Reached!")
		}

		if (shouldSendTitle) {
			ChatUtils.sendAlert(Component.literal("$score").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)))
			MC.player?.makeSound(SoundEvents.EXPERIENCE_ORB_PICKUP)
		}
	}

	private fun onWorldChange() {
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

		mimicKilled = false
		isPaul = false
		princeKilled = false
		crypts = 0

		mimicOpenTime = 0L
		mimicPos = null

		sent270Message = false
		sent300Message = false
		sentMimicMessage = false
	}

	private fun onBlockChange(pos: BlockPos, old: BlockState, new: BlockState) {
		if (!isActive) return
		if (old.block == Blocks.TRAPPED_CHEST && new.block == Blocks.AIR) {
			mimicOpenTime = Util.getMillis()
			mimicPos = pos
		}
	}

	private fun checkMimicDead(client: Minecraft) {
		if (mimicOpenTime == 0L || mimicKilled) return
		if (Util.getMillis() - mimicOpenTime < 750) return

		val playerDistanceFromMimic = client.player?.distanceToSqr(mimicPos?.center ?: return) ?: return
		if (playerDistanceFromMimic >= 400.0) return

		val isMimicDead = client.level?.entitiesForRendering()?.none { it is Zombie && it.isBaby && it.headTexture == HeadTextures.MIMIC }
		if (isMimicDead == true)
			setMimicDead(config.mimicMessage)
	}

	private fun onEntityDespawn(entity: Entity) {
		if (!isActive) return
		if (entity is Zombie && entity.isBaby && entity.headTexture == HeadTextures.MIMIC) {
			setMimicDead(config.mimicMessage)
		}
	}

	private fun setMimicDead(withMessage: Boolean) {
		mimicKilled = true
		if (withMessage && !sentMimicMessage) {
			Utils.runCommand("/pc Mimic Killed!")
			sentMimicMessage = true
		}
	}

	private fun onPuzzleReset() {
		if (!isActive) return
		missingPuzzles = (missingPuzzles + 1)
		failedPuzzles = (failedPuzzles - 1).coerceAtLeast(0)
	}

	private fun onScoreboardChange(line: String) {
		if (!isActive) return

		if (line.startsWith("Cleared: ")) {
			val matcher = dungeonClearedPattern.find(line)
			if (matcher != null) {
				clearedPercentage = matcher.groups["percentage"]?.value?.toIntOrNull() ?: 0
				return
			}
		}
	}

	private fun onPlayerListChange(entries: List<ClientboundPlayerInfoUpdatePacket.Entry>) {
		if (!isActive) return

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

	private fun onMessageReceived(text: Component, actionBar: Boolean) {
		if (!isActive || actionBar) return
		val message = text.string

		if (message == "A Prince falls. +1 Bonus Score") {
			princeKilled = true
			if (config.princeMessage) Utils.runCommand("/pc Prince Killed!")
		}

		if (message.startsWith("§9Party §8>")) {
			if (message.contains(skytilsMimicMessage) || message.contains(mimicMessage)) {
				setMimicDead(false)
			} else if (message.contains(princeMessage)) {
				princeKilled = true
			}
		}
	}

	private fun onTick(client: Minecraft) {
		if (!isActive) return

		if (isMimicFloor) checkMimicDead(client)

		if (config.scoreMessage300 != ScoreMessageType.DISABLED || config.scoreMessage270 != ScoreMessageType.DISABLED) {
			when {
				totalScore >= 300 && !sent300Message -> {
					sendScoreMessage(300, config.scoreMessage300)
					sent300Message = true
					sent270Message = true
				}

				totalScore >= 270 && !sent270Message -> {
					sendScoreMessage(270, config.scoreMessage270)
					sent270Message = true
				}
			}
		}
	}

	override fun render(drawContext: GuiGraphics, deltaTicks: Float) {
		if (!isActive || config.scoreHudType == ScoreHudType.DISABLED) return
		drawContext.pose().pushMatrix()
		applyTransformations(drawContext.pose())

		if (config.scoreHudType == ScoreHudType.FULL) {

			val status = setOf(
				"§9Dungeon Status",
				"§f* §eDeaths: §c$deaths",
				"§f* §eMissing Puzzles: §c$missingPuzzles",
				"§f* §eFailed Puzzles: §c$failedPuzzles",
				"§f* §eSecrets: §a$foundSecrets§7/§a$secretsNeeded §7(§6Total: $totalSecrets§7)",
				"§f* §eCrypts: §a$crypts",
				"§f* §ePrince: ${if (princeKilled) "§a✔" else "§c✖"}",
			)

			val score = setOf(
				"§9Score",
				"§f* §eSkill Score: §a$skillScore",
				"§f* §eExplore Score: §a$exploreScore §7(§e$roomClearScore §7+ §6$secretScore§7)",
				"§f* §eSpeed Score: §a$speedScore",
				"§f* §eBonus Score: §a$bonusScore",
				"§f* §eTotal Score: §a$totalScore",
				"§f* §eRank: $rank",
			)

			val lines = buildList {
				addAll(status)
				if (isMimicFloor) add("§f* §eMimic: ${if (mimicKilled) "§a✔" else "§c✖"}")
				add("")
				addAll(score)
			}

			for ((index, line) in lines.withIndex()) {
				drawContext.drawText(line, 2, 2 + (index * 10), CommonColors.WHITE, true)
			}

		} else {
			drawContext.drawText("§eScore: §a$totalScore §7($rank§7)", 2, 2, CommonColors.WHITE, true)
		}

		drawContext.pose().popMatrix()
	}
}