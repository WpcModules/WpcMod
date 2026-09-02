package net.wapic.wpcmod.features.dungeons.funnymap.dungeon

import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.util.Util
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.chunk.EmptyLevelChunk
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.ScoreCalculation
import net.wapic.wpcmod.features.dungeons.funnymap.core.DungeonPlayer
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.*
import net.wapic.wpcmod.features.dungeons.funnymap.utils.MapUtils
import net.wapic.wpcmod.util.DungeonUtils.DungeonClass
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.TabListUtil
import net.wapic.wpcmod.util.Utils.equalsOneOf

object MapUpdate {

	var roomAdded = false

	fun preloadHeads() {
		val tabEntries = TabListUtil.getDungeonTabList() ?: return
		for (i in listOf(5, 9, 13, 17, 1)) {
			tabEntries[i].first.skin
		}
	}

	fun getPlayers(tabEntries: List<Pair<PlayerInfo, Component>>) {
		var iconNum = 0
		for (i in listOf(5, 9, 13, 17, 1)) {
			with(tabEntries[i]) {
				val name = second.string.trim().substringAfterLast("] ").split(" ")[0]
				if (name != "") {
					FunnyMap.dungeonTeammates[name] = DungeonPlayer(first.skin).apply {
						MC.level?.players()?.find { it.name.string == name }?.let { setData(it) }
						colorPrefix = second.string.substringBefore(name, "f").last()
						this.name = name
						icon = "icon-$iconNum"
					}
					iconNum++
				}
			}
		}
	}

	fun updatePlayers(tabEntries: List<Pair<PlayerInfo, Component>>) {
		if (FunnyMap.dungeonTeammates.isEmpty()) return
		val time = Util.getMillis() - FunnyMap.Info.startTime

		for ((index, value) in listOf(5, 9, 13, 17, 1).withIndex()) {
			val tabText = tabEntries[value].second.string.trim()
			val name = tabText.substringAfterLast("] ").split(" ")[0]
			if (name.isEmpty()) continue

			FunnyMap.dungeonTeammates[name]?.run {
				dead = tabText.contains("(DEAD)")
				if (dead) continue

				if (dungeonClass == DungeonClass.EMPTY) {
					val classText = tabText.substringAfter("(").substringBefore(")").substringBefore(" ")
					dungeonClass = DungeonClass.fromTabText(classText)
				}

				val player = MC.level?.players()?.find { it.stringUUID == uuid }?.let {
					if (!playerLoaded) setData(it)
					this.updatePos(
						((it.x - DungeonScan.START_X + 13) * MapUtils.coordMultiplier + MapUtils.startCorner.first).toFloat(),
						((it.z - DungeonScan.START_Z + 13) * MapUtils.coordMultiplier + MapUtils.startCorner.second).toFloat(),
						it.yRot
					)
					return@let it
				}

				if (player == null) {
					MapUtils.mapData?.decorations?.elementAtOrNull(index)?.let { decoration ->
						if (decoration.type == MapDecorationTypes.FRAME) return@let // no need to update local player from map
						this.updatePos(
							((decoration.x + 128) shr 1).toFloat(),
							((decoration.y + 128) shr 1).toFloat(),
							decoration.rot * 22.5f
						)
					}
				}

				val room = getCurrentRoom()
				room?.let { current ->
					if (time <= 1000) return@let
					if (lastRoom == null) {
						lastRoom = current
					} else if (lastRoom?.data?.name != current.data.name) {

						if (current.state.equalsOneOf(RoomState.UNDISCOVERED, RoomState.UNOPENED)) {
							current.uniqueRoom?.setRoomState(RoomState.DISCOVERED)
						}

						lastRoom?.let { last ->
							if (isPlayer) {
								DungeonEvents.ROOM_ENTERED.invoker().onRoomEntered(last, current)
							}
							roomVisits.add(Pair(time - lastTime, last))
							lastTime = time
							lastRoom = current
						}
					}
				}
			}
		}
	}

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
