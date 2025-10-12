package net.wapic.wpcmod.features.funnymap.dungeon

import net.wapic.wpcmod.features.funnymap.core.RoomData
import net.wapic.wpcmod.features.funnymap.core.map.Room
import net.wapic.wpcmod.features.funnymap.core.map.RoomState
import net.wapic.wpcmod.features.funnymap.core.map.Tile
import net.wapic.wpcmod.util.Utils.equalsOneOf

object PlayerTracker {

	val roomClears: MutableMap<RoomData, Set<String>> = mutableMapOf()

	fun roomStateChange(room: Tile, state: RoomState, newState: RoomState) {
		if (room !is Room) return
		if (newState.equalsOneOf(RoomState.CLEARED, RoomState.GREEN) && state != RoomState.CLEARED) {
			val currentRooms =
				Dungeon.dungeonTeammates.map { Pair(it.value.formattedName, it.value.getCurrentRoom()) }
			roomClears[room.data] =
				currentRooms.filter { it.first != "" && it.second == room.data.name }.map { it.first }.toSet()
		}
	}
}
