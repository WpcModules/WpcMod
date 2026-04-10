package net.wapic.wpcmod.util

import net.minecraft.client.Minecraft
import net.minecraft.world.scores.DisplaySlot
import net.minecraft.world.scores.PlayerScoreEntry
import net.minecraft.world.scores.PlayerTeam
import net.wapic.wpcmod.util.ChatUtils.removeFormatting

object ScoreboardUtil {

	fun cleanSB(scoreboard: String): String {
		return scoreboard.removeFormatting().toCharArray().filter { it.code in 32..126 }.joinToString(separator = "")
	}

	var sidebarLines: List<String> = emptyList()

	private val SCOREBOARD_ENTRY_COMPARATOR: Comparator<PlayerScoreEntry> =
		Comparator.comparing { obj: PlayerScoreEntry -> obj.value() }
			.reversed()
			.thenComparing({ obj: PlayerScoreEntry -> obj.owner() }, java.lang.String.CASE_INSENSITIVE_ORDER)

	fun fetchScoreboardLines(): List<String> {
		val mc = Minecraft.getInstance()
		val scoreboard = mc.level?.scoreboard ?: return emptyList()
		val objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR) ?: return emptyList()
		val scores =
			scoreboard.listPlayerScores(objective).filter { !it.isHidden }.sortedWith(SCOREBOARD_ENTRY_COMPARATOR)
				.take(15)

		return scores.map { e ->
			PlayerTeam.formatNameForTeam(scoreboard.getPlayersTeam(e.owner()), e.ownerName()).string
		}.asReversed()
	}
}