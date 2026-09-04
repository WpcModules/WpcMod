package net.wapic.wpcmod.features.dungeons.funnymap.dungeon

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.chunk.EmptyLevelChunk
import net.wapic.wpcmod.features.dungeons.ScoreCalculation
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.*
import net.wapic.wpcmod.features.dungeons.funnymap.utils.MapUtils
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.equalsOneOf

object MapUpdate {

	var roomAdded = false

	fun updateRooms() {
		if (FunnyMap.Info.ended) return
		val map = DungeonMap(MapUtils.mapData?.colors ?: return)
		FunnyMap.espDoors.clear()

		for (x in 0..10) {
			for (z in 0..10) {
				val room = FunnyMap.Info.dungeonList[z * 11 + x]
				val mapTile = map.getTile(x, z)

				if (room is Unknown) {
					roomAdded = true
					FunnyMap.Info.dungeonList[z * 11 + x] = mapTile
					continue
				}

				if (mapTile.state.ordinal < room.state.ordinal) {
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
					} else if (!room.opened && MC.level?.getChunk(room.x shr 4, room.z shr 4) !is EmptyLevelChunk &&
						MC.level?.getBlockState(BlockPos(room.x, 69, room.z))?.block == Blocks.AIR
					) {
						room.opened = true
					}

					if (!room.opened) {
						FunnyMap.espDoors.add(room)
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

				val room = FunnyMap.Info.dungeonList[index]
				if (room !is Room) continue

				val connected = getConnectedIndices(x, z)
				var unique = room.uniqueRoom
				if (unique == null || unique.name.startsWith("Unknown")) {
					unique = connected.firstOrNull {
						(FunnyMap.Info.dungeonList[it.second * 11 + it.first] as? Room)?.uniqueRoom?.name?.startsWith("Unknown") == false
					}?.let {
						(FunnyMap.Info.dungeonList[it.second * 11 + it.first] as? Room)?.uniqueRoom
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
		val tile = FunnyMap.Info.dungeonList[arrayY * 11 + arrayX]
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
				if (FunnyMap.Info.dungeonList[y * 11 + x] is Room) {
					queue.add(Pair(x, y))
				}
			}
		}
		return connected
	}
}
