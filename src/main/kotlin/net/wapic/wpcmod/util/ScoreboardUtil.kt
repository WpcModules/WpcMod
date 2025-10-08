package net.wapic.wpcmod.util

import net.minecraft.client.MinecraftClient
import net.minecraft.scoreboard.ScoreboardDisplaySlot
import net.minecraft.scoreboard.ScoreboardEntry
import net.minecraft.scoreboard.Team
import net.wapic.wpcmod.util.ChatUtils.removeFormatting

object ScoreboardUtil {

	fun cleanSB(scoreboard: String): String {
		return scoreboard.removeFormatting().toCharArray().filter { it.code in 32..126 }.joinToString(separator = "")
	}

	var sidebarLines: List<String> = emptyList()

	private val SCOREBOARD_ENTRY_COMPARATOR: Comparator<ScoreboardEntry> =
		Comparator.comparing { obj: ScoreboardEntry -> obj.value() }
			.reversed()
			.thenComparing({ obj: ScoreboardEntry -> obj.owner() }, java.lang.String.CASE_INSENSITIVE_ORDER)

	fun fetchScoreboardLines(): List<String> {
		val mc = MinecraftClient.getInstance()
		val scoreboard = mc.world?.scoreboard ?: return emptyList()
		val objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR) ?: return emptyList()
		val scores = scoreboard.getScoreboardEntries(objective).filter { input ->
			input?.owner != null && !input.hidden()
		}.sortedWith(SCOREBOARD_ENTRY_COMPARATOR).take(15)
		return scores.map { e ->
			Team.decorateName(scoreboard.getScoreHolderTeam(e.owner()), e.name()).string
		}.asReversed()
	}
}