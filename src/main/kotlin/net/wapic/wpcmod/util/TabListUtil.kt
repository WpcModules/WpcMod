package net.wapic.wpcmod.util

import com.google.common.collect.ComparisonChain
import com.google.common.collect.Ordering
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.network.chat.Component
import net.minecraft.world.level.GameType

object TabListUtil {
	private val tabListOrder = Ordering.from<PlayerInfo> { o1, o2 ->
		if (o1 == null) return@from -1
		if (o2 == null) return@from 0
		return@from ComparisonChain.start().compareTrueFirst(
			o1.gameMode != GameType.SPECTATOR, o2.gameMode != GameType.SPECTATOR
		).compare(
			o1.team?.name ?: "", o2.team?.name ?: ""
		).compare(o1.profile.name, o2.profile.name).result()
	}

	fun getTabList(): List<Pair<PlayerInfo, Component>> {
		return MC.player?.connection?.onlinePlayers?.let {
			tabListOrder.immutableSortedCopy(it)
		}?.map { Pair(it, MC.gui.hud.tabList.getNameForDisplay(it)) } ?: emptyList()
	}

	fun getDungeonTabList(): List<Pair<PlayerInfo, Component>>? {
		return getTabList().let { if (it.size > 18 && it[0].second.string.contains("Party (")) it else null }
	}
}