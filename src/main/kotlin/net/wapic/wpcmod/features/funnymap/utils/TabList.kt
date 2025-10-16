package net.wapic.wpcmod.features.funnymap.utils

import com.google.common.collect.ComparisonChain
import com.google.common.collect.Ordering
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.text.Text
import net.minecraft.world.GameMode
import net.wapic.wpcmod.util.MC

object TabList {
	private val tabListOrder = Ordering.from<PlayerListEntry> { o1, o2 ->
		if (o1 == null) return@from -1
		if (o2 == null) return@from 0
		return@from ComparisonChain.start().compareTrueFirst(
			o1.gameMode != GameMode.SPECTATOR, o2.gameMode != GameMode.SPECTATOR
		).compare(
			o1.scoreboardTeam?.name ?: "", o2.scoreboardTeam?.name ?: ""
		).compare(o1.profile.name, o2.profile.name).result()
	}

	fun getTabList(): List<Pair<PlayerListEntry, Text>> {
		return MC.player?.networkHandler?.playerList?.let {
			tabListOrder.immutableSortedCopy(it)
		}?.map { Pair(it, MC.inGameHud.playerListHud.getPlayerName(it)) } ?: emptyList()
	}

	fun getDungeonTabList(): List<Pair<PlayerListEntry, Text>>? {
		return getTabList().let { if (it.size > 18 && it[0].second.string.contains("Party (")) it else null }
	}
}
