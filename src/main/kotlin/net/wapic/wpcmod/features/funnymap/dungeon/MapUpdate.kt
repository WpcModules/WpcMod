package net.wapic.wpcmod.features.funnymap.dungeon

import net.minecraft.block.Blocks
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.item.map.MapDecorationTypes
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.chunk.EmptyChunk
import net.wapic.wpcmod.features.dungeons.ScoreCalculation
import net.wapic.wpcmod.features.funnymap.core.DungeonPlayer
import net.wapic.wpcmod.features.funnymap.core.map.*
import net.wapic.wpcmod.features.funnymap.utils.MapUtils
import net.wapic.wpcmod.features.funnymap.utils.MapUtils.mapX
import net.wapic.wpcmod.features.funnymap.utils.MapUtils.mapZ
import net.wapic.wpcmod.features.funnymap.utils.MapUtils.yaw
import net.wapic.wpcmod.features.funnymap.utils.TabList
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.equalsOneOf
import kotlin.math.roundToInt

object MapUpdate {
	var roomAdded = false

	fun preloadHeads() {
		val tabEntries = TabList.getDungeonTabList() ?: return
		for (i in listOf(5, 9, 13, 17, 1)) {
			tabEntries[i].first.skinTextures
		}
	}

	fun getPlayers() {
		val tabEntries = TabList.getDungeonTabList() ?: return
		Dungeon.dungeonTeammates.clear()
		var iconNum = 0
		for (i in listOf(5, 9, 13, 17, 1)) {
			with(tabEntries[i]) {
				val name = second.string.trim().substringAfterLast("] ").split(" ")[0]
				if (name != "") {
					Dungeon.dungeonTeammates[name] = DungeonPlayer(first.skinTextures).apply {
						MC.world?.players?.find { it.name.string == name }?.let { setData(it) }
						colorPrefix = second.string.substringBefore(name, "f").last()
						this.name = name
						icon = "icon-$iconNum"
					}
					iconNum++
				}
			}
		}
	}

	fun updatePlayers(tabEntries: List<Pair<PlayerListEntry, Text>>) {
		if (Dungeon.dungeonTeammates.isEmpty()) return
		// Update map icons
		val time = System.currentTimeMillis() - Dungeon.Info.startTime
		var iconNum = 0
		for (i in listOf(5, 9, 13, 17, 1)) {
			val tabText = tabEntries[i].second.string.trim()
			val name = tabText.substringAfterLast("] ").split(" ")[0]
			if (name == "") continue
			Dungeon.dungeonTeammates[name]?.run {
				dead = tabText.contains("(DEAD)")
				if (dead) {
					icon = ""
				} else {
					icon = "icon-$iconNum"
					iconNum++
				}
				if (!playerLoaded) {
					MC.world?.players?.find { it.name.string == name }?.let { setData(it) }
				}

				val room = getCurrentRoom()
				if (room != "Error" || time > 1000) {
					if (lastRoom == "") {
						lastRoom = room
					} else if (lastRoom != room) {
						roomVisits.add(Pair(time - lastTime, lastRoom))
						lastTime = time
						lastRoom = room
					}
				}
			}
		}

		val decor = MapUtils.mapData?.decorations ?: return
		val mcPlayer = MC.player ?: return
		Dungeon.dungeonTeammates.forEach { (name, player) ->
			decor.find { mapDecoration ->
				mapDecoration.assetId.toString() == player.icon
			}?.let { decoration ->
				player.isPlayer = decoration.type == MapDecorationTypes.PLAYER
				player.mapX = decoration.mapX
				player.mapZ = decoration.mapZ
				player.yaw = decoration.yaw
			}
			if (player.isPlayer || name == mcPlayer.name?.string) {
				player.yaw = mcPlayer.yaw
				player.mapX =
					((mcPlayer.pos.x - DungeonScan.START_X + 15) * MapUtils.coordMultiplier + MapUtils.startCorner.first).roundToInt()
				player.mapZ =
					((mcPlayer.pos.z - DungeonScan.START_Z + 15) * MapUtils.coordMultiplier + MapUtils.startCorner.second).roundToInt()
			}
		}
	}

	fun updateRooms() {
		if (Dungeon.Info.ended) return
		val map = DungeonMap(MapUtils.mapData?.colors ?: return)
		Dungeon.espDoors.clear()

		for (x in 0..10) {
			for (z in 0..10) {
				val room = Dungeon.Info.dungeonList[z * 11 + x]
				val mapTile = map.getTile(x, z)

				if (room is Unknown) {
					roomAdded = true
					Dungeon.Info.dungeonList[z * 11 + x] = mapTile
					continue
				}

				if (mapTile.state.ordinal < room.state.ordinal) {
					PlayerTracker.roomStateChange(room, room.state, mapTile.state)
					if (room is Room && room.data.type == RoomType.BLOOD && mapTile.state == RoomState.GREEN) {
						ScoreCalculation.bloodCleared = true
					}
					room.state = mapTile.state
				}

				if (mapTile is Room && room is Room) {
					if (room.data.type != mapTile.data.type && mapTile.data.type != RoomType.NORMAL) {
						room.data.type = mapTile.data.type
					}
				}

				if (mapTile is Door && room is Door) {
					if (mapTile.type == DoorType.WITHER && room.type != DoorType.WITHER) {
						room.type = mapTile.type
					}
				}

				if (room is Door && room.type.equalsOneOf(DoorType.ENTRANCE, DoorType.WITHER, DoorType.BLOOD)) {
					if (mapTile is Door && mapTile.type == DoorType.WITHER) {
						if (room.opened) {
							room.opened = false
						}
					} else if (!room.opened && MC.world?.getChunk(room.x shr 4, room.z shr 4) !is EmptyChunk &&
						MC.world?.getBlockState(BlockPos(room.x, 69, room.z))?.block == Blocks.AIR
					) {
						room.opened = true
					}

					if (!room.opened) {
						Dungeon.espDoors.add(room)
					}
				}
			}
		}

		if (roomAdded) {
			updateUniques()
		}
	}

	fun updateUniques() {
		val visited = BooleanArray(121)
		for (x in 0..10) {
			for (z in 0..10) {
				val index = z * 11 + x
				if (visited[index]) continue
				visited[index] = true

				val room = Dungeon.Info.dungeonList[index]
				if (room !is Room) continue

				val connected = getConnectedIndices(x, z)
				var unique = room.uniqueRoom
				if (unique == null || unique.name.startsWith("Unknown")) {
					unique = connected.firstOrNull {
						(Dungeon.Info.dungeonList[it.second * 11 + it.first] as? Room)?.uniqueRoom?.name?.startsWith("Unknown") == false
					}?.let {
						(Dungeon.Info.dungeonList[it.second * 11 + it.first] as? Room)?.uniqueRoom
					} ?: unique
				}

				val finalUnique = unique ?: UniqueRoom(x, z, room)

				finalUnique.addTiles(connected)

				connected.forEach {
					visited[it.second * 11 + it.first] = true
				}
			}
		}
		roomAdded = false
	}

	private fun getConnectedIndices(arrayX: Int, arrayY: Int): List<Pair<Int, Int>> {
		val tile = Dungeon.Info.dungeonList[arrayY * 11 + arrayX]
		if (tile !is Room) return emptyList()
		val directions = listOf(
			Pair(0, 1),
			Pair(1, 0),
			Pair(0, -1),
			Pair(-1, 0)
		)
		val connected = mutableListOf<Pair<Int, Int>>()
		val queue = mutableListOf(Pair(arrayX, arrayY))
		while (queue.isNotEmpty()) {
			val current = queue.removeFirst()
			if (connected.contains(current)) continue
			connected.add(current)
			directions.forEach {
				val x = current.first + it.first
				val y = current.second + it.second
				if (x !in 0..10 || y !in 0..10) return@forEach
				if (Dungeon.Info.dungeonList[y * 11 + x] is Room) {
					queue.add(Pair(x, y))
				}
			}
		}
		return connected
	}
}
