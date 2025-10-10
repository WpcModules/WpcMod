package net.wapic.wpcmod.features.funnymap.ui

import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.funnymap.FunnyMap.mc
import net.wapic.wpcmod.features.funnymap.features.dungeon.RunInformation
import net.wapic.wpcmod.features.funnymap.features.dungeon.ScoreCalculation
import net.wapic.wpcmod.jarvis.SimpleHudElement
import net.wapic.wpcmod.util.DungeonUtils

object ScoreElement : SimpleHudElement(
	text = Text.literal("Dungeon Map"),
	w = 128,
	h = 64
) {

	val fr: TextRenderer = mc.textRenderer
	val config get() = WpcMod.config.funnyMap
	var y2 = 0

	fun init() {
		HudLayerRegistrationCallback.EVENT.register { layeredDrawer ->
			layeredDrawer.attachLayerBefore(
				IdentifiedLayer.CHAT,
				IdentifiedLayer.of(Identifier.of("wpcmod", "funny_score_calc"), ::render)
			)
		}
	}

	private var elementLines = 1
		set(value) {
			if (field != value) {
				field = value
				y2 = (y + h * scale).toInt()
			}
		}

	fun render(drawContext: DrawContext, tickCounter: RenderTickCounter) {
		var y = 0f
		val lines = getScoreLines()
		elementLines = lines.size
		lines.forEach {
			drawContext.drawText(fr, it, 0, y.toInt(), 0xffffff, true)
			y += fr.fontHeight
		}
	}

	override fun isActive(): Boolean {
		if (!isEnabled) return false
		if (config.scoreHideInBoss && DungeonUtils.isBossSpawned()) return false
		return super.isActive()
	}

	override fun isEnabled(): Boolean {
		return config.scoreElementEnabled
	}

	fun getScoreLines(): List<String> {
		val list: MutableList<String> = mutableListOf()

		if (config.scoreTotalScore) {
			list.add(getScore(config.scoreMinimizedName, true))
		}

		if (config.scoreSecrets) {
			list.add(getSecrets(config.scoreMinimizedName, true))
		}

		if (config.scoreCrypts) {
			list.add(getCrypts(config.scoreMinimizedName))
		}

		if (config.scoreMimic) {
			list.add(getMimic(config.scoreMinimizedName))
		}

		if (config.scoreDeaths) {
			list.add(getDeaths(config.scoreMinimizedName))
		}

		if (config.scorePuzzles) {
			list.add(getPuzzles(config.scoreMinimizedName, true))
		}

		return list
	}

	fun runInformationLines(): List<String> {
		val list: MutableList<String> = mutableListOf()

		if (config.runInformationScore) {
			list.add(getScore(minimized = false, expanded = false))
		}

		if (config.runInformationSecrets) {
			list.add(getSecrets(minimized = false, missing = true))
		}

		list.add("split")

		if (config.runInformationCrypts) {
			list.add(getCrypts())
		}

		if (config.runInformationMimic) {
			list.add(getMimic())
		}

		if (config.runInformationDeaths) {
			list.add(getDeaths())
		}

		return list
	}

	private fun getScore(minimized: Boolean = false, expanded: Boolean): String {
		val scoreColor = when {
			ScoreCalculation.score < 270 -> "§c"
			ScoreCalculation.score < 300 -> "§e"
			else -> "§a"
		}
		var line = if (minimized) "" else "§7Score: "
		if (expanded) {
			line += "§b${ScoreCalculation.getSkillScore()}§7/" +
					"§a${ScoreCalculation.getExplorationScore()}§7/" +
					"§3${ScoreCalculation.getSpeedScore(RunInformation.timeElapsed)}§7/" +
					"§d${ScoreCalculation.getBonusScore()} §7: "
		}
		line += "$scoreColor${ScoreCalculation.score}"

		return line
	}

	private fun getSecrets(minimized: Boolean = false, missing: Boolean): String {
		var line = if (minimized) "" else "§7Secrets: "
		line += "§b${RunInformation.secretsFound}§7/"
		if (missing) {
			val missingSecrets = (RunInformation.minSecrets - RunInformation.secretsFound).coerceAtLeast(0)
			line += "§e${missingSecrets}§7/"
		}
		line += "§c${RunInformation.secretTotal}"

		return line
	}

	private fun getCrypts(minimized: Boolean = false): String {
		var line = if (minimized) "§7C: " else "§7Crypts: "
		line += if (RunInformation.cryptsCount >= 5) "§a${RunInformation.cryptsCount}" else "§c${RunInformation.cryptsCount}"
		return line
	}

	private fun getMimic(minimized: Boolean = false): String {
		var line = if (minimized) "§7M: " else "§7Mimic: "
		line += if (RunInformation.mimicKilled) "§a✔" else "§c✘"
		return line
	}

	private fun getDeaths(minimized: Boolean = false): String {
		var line = if (minimized) "§7D: " else "§7Deaths: "
		line += "§c${RunInformation.deathCount}"
		return line
	}

	private fun getPuzzles(minimized: Boolean = false, total: Boolean): String {
		val color = if (RunInformation.completedPuzzles == RunInformation.totalPuzzles) "§a" else "§c"
		var line = if (minimized) "§7P: " else "§7Puzzles: "
		line += "$color${RunInformation.completedPuzzles}"
		if (total) line += "§7/$color${RunInformation.totalPuzzles}"
		return line
	}
}
