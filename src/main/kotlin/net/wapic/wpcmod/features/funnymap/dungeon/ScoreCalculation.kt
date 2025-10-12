package net.wapic.wpcmod.features.funnymap.dungeon

import net.minecraft.sound.SoundEvents
import net.minecraft.text.Style
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.funnymap.dungeon.RunInformation.completedRoomsPercentage
import net.wapic.wpcmod.features.funnymap.dungeon.RunInformation.mimicKilled
import net.wapic.wpcmod.features.funnymap.dungeon.RunInformation.secretPercentage
import net.wapic.wpcmod.util.APIUtils
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.Utils.runCommand
import net.wapic.wpcmod.util.MC
import kotlin.math.roundToInt
import kotlin.time.DurationUnit
import kotlin.time.toDuration

object ScoreCalculation {
	val FunnyConfig get() = WpcMod.config.funnyMap

	val paul = APIUtils.hasBonusPaulScore()
		get() = field || FunnyConfig.paulBonus
	var score = 0
	var message300 = false
	var message270 = false

	fun updateScore() {
		score = getSkillScore() + getExplorationScore() + getSpeedScore(RunInformation.timeElapsed) + getBonusScore()
		if (score >= 300 && !message300) {
			message300 = true
			message270 = true
			if (FunnyConfig.scoreMessage300) {
				runCommand("/pc ${FunnyConfig.message300}")
			}

			if (FunnyConfig.scoreTitle300) {
				MC.player?.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.5.toFloat())
				ChatUtils.sendAlert(FunnyConfig.message300, Style.EMPTY)
			}
			if (FunnyConfig.timeTo300) {
				ChatUtils.sendMessage("§3300 Score§7: §a${RunInformation.timeElapsed.toDuration(DurationUnit.SECONDS)}")
			}
		} else if (score >= 270 && !message270) {
			message270 = true
			if (FunnyConfig.scoreMessage270) {
				runCommand("/pc ${FunnyConfig.message270}")
			}
			if (FunnyConfig.scoreTitle270) {
				MC.player?.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.5.toFloat())
				ChatUtils.sendAlert(FunnyConfig.message270, Style.EMPTY)
			}
		}
	}

	fun getSkillScore(): Int {
		val puzzleDeduction = (RunInformation.totalPuzzles - RunInformation.completedPuzzles) * 10
		val roomPercent = completedRoomsPercentage.coerceAtMost(1f)
		return 20 + ((80 * roomPercent).toInt() - puzzleDeduction - getDeathDeduction()).coerceAtLeast(0)
	}

	fun getDeathDeduction(): Int {
		var deathDeduction = RunInformation.deathCount * 2
		if (FunnyConfig.scoreAssumeSpirit) deathDeduction -= 1
		return deathDeduction.coerceAtLeast(0)
	}

	fun getExplorationScore(): Int {
		val secretPercent = (secretPercentage / getSecretPercent()).coerceAtMost(1f)
		val roomPercent = completedRoomsPercentage.coerceAtMost(1f)
		return (60 * roomPercent + 40 * secretPercent).toInt()
	}

	fun getSpeedScore(timeElapsed: Int): Int {
		var score = 100
		val limit = getTimeLimit()
		if (timeElapsed < limit) return score
		val percentageOver = (timeElapsed - limit) * 100f / limit
		score -= getSpeedDeduction(percentageOver).toInt()
		return if (DungeonUtils.currentFloor == DungeonUtils.DungeonFloor.ENTRANCE) (score * 0.7).roundToInt() else score
	}

	fun getBonusScore(): Int {
		var score = 0
		score += RunInformation.cryptsCount.coerceAtMost(5)
		if (mimicKilled) score += 2
		if (paul) score += 10
		return score
	}

	fun getSecretPercent(): Float {
		return when (DungeonUtils.currentFloor) {
			DungeonUtils.DungeonFloor.ENTRANCE -> .3f
			DungeonUtils.DungeonFloor.FLOOR_1 -> .3f
			DungeonUtils.DungeonFloor.FLOOR_2 -> .4f
			DungeonUtils.DungeonFloor.FLOOR_3 -> .5f
			DungeonUtils.DungeonFloor.FLOOR_4 -> .6f
			DungeonUtils.DungeonFloor.FLOOR_5 -> .7f
			DungeonUtils.DungeonFloor.FLOOR_6 -> .85f
			else -> 1f
		}
	}

	private fun getTimeLimit(): Int {
		return when (DungeonUtils.currentFloor) {
			DungeonUtils.DungeonFloor.ENTRANCE -> 1320
			DungeonUtils.DungeonFloor.FLOOR_1,
			DungeonUtils.DungeonFloor.FLOOR_2,
			DungeonUtils.DungeonFloor.FLOOR_3,
			DungeonUtils.DungeonFloor.FLOOR_5 -> 600
			DungeonUtils.DungeonFloor.FLOOR_4, DungeonUtils.DungeonFloor.FLOOR_6 -> 720
			DungeonUtils.DungeonFloor.MASTER_MODE_FLOOR_1,
			DungeonUtils.DungeonFloor.MASTER_MODE_FLOOR_2,
			DungeonUtils.DungeonFloor.MASTER_MODE_FLOOR_3,
			DungeonUtils.DungeonFloor.MASTER_MODE_FLOOR_4,
			DungeonUtils.DungeonFloor.MASTER_MODE_FLOOR_5 -> 480
			DungeonUtils.DungeonFloor.MASTER_MODE_FLOOR_6 -> 600
			else -> 840
		}
	}

	/**
	 * This is a very ugly function, but it works.
	 * The formula on the wiki doesn't seem to work, this variation should never be more than 2 points off.
	 */
	private fun getSpeedDeduction(percentage: Float): Float {
		var percentageOver = percentage
		var deduction = 0f

		deduction += (percentageOver.coerceAtMost(20f) / 2f)
		percentageOver -= 20f
		if (percentageOver <= 0) return deduction

		deduction += (percentageOver.coerceAtMost(20f) / 3.5f)
		percentageOver -= 20f
		if (percentageOver <= 0) return deduction

		deduction += (percentageOver.coerceAtMost(10f) / 4f)
		percentageOver -= 10f
		if (percentageOver <= 0) return deduction

		deduction += (percentageOver.coerceAtMost(10f) / 5f)
		percentageOver -= 10f
		if (percentageOver <= 0) return deduction

		deduction += (percentageOver / 6f)
		return deduction
	}
}
