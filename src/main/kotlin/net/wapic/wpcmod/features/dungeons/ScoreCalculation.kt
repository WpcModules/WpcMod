package net.wapic.wpcmod.features.dungeons

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.world.ClientWorld
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.wapic.wpcmod.events.PlayerListChangeEvent
import net.wapic.wpcmod.events.ScoreboardChangeEvent
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils
import kotlin.math.floor

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

	private var mimicFound = false
	private var isPaul = false

	private var crypts = 0
	private var deaths = 0
	private var missingPuzzles = 0
	private var failedPuzzles = 0
	private var completedRooms = 0
	private var clearedPercentage = 0
	private var foundSecrets = 0
	private var totalSecrets = 0
	private var secondsElapsed = 0.0

	init {
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
		WorldChangeEvent.EVENT.register(::onWorldChange)
		PlayerListChangeEvent.EVENT.register(::onPlayerListChange)
		ScoreboardChangeEvent.EVENT.register(::onScoreboardChange)

		HudLayerRegistrationCallback.EVENT.register { layeredDrawer ->
			layeredDrawer.attachLayerBefore(
				IdentifiedLayer.CHAT,
				IdentifiedLayer.of(Identifier.of("wpcmod", "score_calc"), ::onRenderHud)
			)
		}
	}

	private fun onWorldChange(client: MinecraftClient, world: ClientWorld) {
		mimicFound = false
		isPaul = false

		crypts = 0
		deaths = 0
		missingPuzzles = 0
		failedPuzzles = 0
		completedRooms = 0
		clearedPercentage = 0
		foundSecrets = 0
		totalSecrets = 0
		secondsElapsed = 0.0

	}

	private fun onScoreboardChange(line: String) {
		if (Utils.getLocation() != Island.DUNGEON) return

		println("received $line")

		if (line.startsWith("Cleared: ")) {
			val matcher = dungeonClearedPattern.find(line)
			if (matcher != null) {
				//start dungeonTimer
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
					//start dungeonTimer
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

		if (message.startsWith("Party >")) {

			if (message.contains(skytilsMimicMessage) || message.contains(mimicMessage)) {
				mimicFound = true
			}
		}
	}

	private fun onRenderHud(drawContext: DrawContext, tickCounter: RenderTickCounter) {
		if (Utils.getLocation() != Island.DUNGEON) return
		val tr = MinecraftClient.getInstance().textRenderer

		drawContext.drawText(tr, "mimic killed: $mimicFound", 2, 28, 0xffffff, true)
		drawContext.drawText(tr, "is paul active: $isPaul", 2, 38, 0xffffff, true)
		drawContext.drawText(tr, "completed rooms: $completedRooms", 2, 48, 0xffffff, true)
		drawContext.drawText(tr, "cleared percentage: $clearedPercentage", 2, 58, 0xffffff, true)
		drawContext.drawText(tr, "missing puzzles: $missingPuzzles", 2, 68, 0xffffff, true)
		drawContext.drawText(tr, "failed puzzles: $failedPuzzles", 2, 78, 0xffffff, true)
		drawContext.drawText(tr, "deaths: $deaths", 2, 88, 0xffffff, true)
		drawContext.drawText(tr, "found secrets: $foundSecrets", 2, 98, 0xffffff, true)
		drawContext.drawText(tr, "total secrets: $totalSecrets", 2, 108, 0xffffff, true)
		drawContext.drawText(tr, "crypts: $crypts", 2, 118, 0xffffff, true)
		drawContext.drawText(tr, "seconds elapsed: $secondsElapsed", 2, 128, 0xffffff, true)
	}
}