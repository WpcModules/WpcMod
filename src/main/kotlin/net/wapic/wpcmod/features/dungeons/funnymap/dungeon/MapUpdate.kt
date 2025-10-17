package net.wapic.wpcmod.features.dungeons.funnymap.dungeon

import net.minecraft.block.Blocks
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.item.map.MapDecorationTypes
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.chunk.EmptyChunk
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.dungeons.ScoreCalculation
import net.wapic.wpcmod.features.dungeons.funnymap.core.DungeonPlayer
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.Door
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.DoorType
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.Room
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.RoomState
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.RoomType
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.UniqueRoom
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.Unknown
import net.wapic.wpcmod.features.dungeons.funnymap.utils.MapUtils
import net.wapic.wpcmod.util.TabListUtil
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.equalsOneOf
import kotlin.math.roundToInt

object MapUpdate {
	var roomAdded = false

	fun preloadHeads() {
		val tabEntries = TabListUtil.getDungeonTabList() ?: return
		for (i in listOf(5, 9, 13, 17, 1)) {
			tabEntries[i].first.skinTextures
		}
	}

	fun getPlayers() {
		val tabEntries = TabListUtil.getDungeonTabList() ?: return
		FunnyMap.dungeonTeammates.clear()
		var iconNum = 0
		for (i in listOf(5, 9, 13, 17, 1)) {
			with(tabEntries[i]) {
				val name = second.string.trim().substringAfterLast("] ").split(" ")[0]
				if (name != "") {
					FunnyMap.dungeonTeammates[name] = DungeonPlayer(first.skinTextures).apply {
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
		if (FunnyMap.dungeonTeammates.isEmpty()) return
		// Update map icons
		val time = System.currentTimeMillis() - FunnyMap.Info.startTime
		var iconNum = 0
		for (i in listOf(5, 9, 13, 17, 1)) {
			val tabText = tabEntries[i].second.string.trim()
			val name = tabText.substringAfterLast("] ").split(" ")[0]
			if (name.isEmpty()) continue
			FunnyMap.dungeonTeammates[name]?.run {
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

		/*
		Dungeon.dungeonTeammates.forEach { (name, player) ->
			MC.world?.players?.find { it.name.string == name }?.let {
				player.isPlayer = it.name.string != MC.player?.name?.string
				player.yaw = it.yaw
				player.mapX = ((it.pos.x - DungeonScan.START_X + 15) * MapUtils.coordMultiplier + MapUtils.startCorner.first).roundToInt()
				player.mapZ = ((it.pos.z - DungeonScan.START_Z + 15) * MapUtils.coordMultiplier + MapUtils.startCorner.second).roundToInt()
			}
		}
		 */


		val decor = MapUtils.mapData?.decorations ?: return

		decor.forEachIndexed { index, decoration ->
			val player = FunnyMap.dungeonTeammates.values.first { it.icon == "icon-$index" }
			player.isPlayer = decoration.type == MapDecorationTypes.FRAME
			if(player.isPlayer) {
				MC.player?.let {
					player.yaw = it.yaw
					player.mapX = ((it.pos.x - DungeonScan.START_X + 15) * MapUtils.coordMultiplier + MapUtils.startCorner.first).roundToInt()
					player.mapZ = ((it.pos.z - DungeonScan.START_Z + 15) * MapUtils.coordMultiplier + MapUtils.startCorner.second).roundToInt()
					return@forEachIndexed
				}
			}
			player.yaw = decoration.rotation * 22.5f
			player.mapX = (decoration.x + 128) shr 1
			player.mapZ = (decoration.z + 128) shr 1
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
					val tile = FunnyMap.Info.dungeonList[it.second * 11 + it.first] as? Room
					// WE'RE CATCHING THIS ISSUE EVENTUALLY
					WpcMod.logger.debug(
						"connected tile details:\nname: {}, color: {}, uniqueName: {}, core: {}, type: {}, cores. {}",
						tile?.data?.name, tile?.color, tile?.uniqueRoom?.name, tile?.core, tile?.data?.type, tile?.data?.cores
					)
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
