package net.wapic.wpcmod.features.dungeons.funnymap.utils

import net.minecraft.world.item.MapItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.saveddata.maps.MapItemSavedData
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.DungeonScan
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.DungeonUtils.inDungeons
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.equalsOneOf

object MapUtils {

	var mapData: MapItemSavedData? = null
	var startCorner = Pair(5, 5)
	var coordMultiplier = 0.625
	var roomSize = 16
	var halfRoomSize = roomSize / 2
	const val CONNECTOR_SIZE = 4
	var calibrated = false
	var mapDataUpdated = false

	private fun getMapItem(): ItemStack? {
		val map = MC.player?.inventory?.getItem(8) ?: return null
		if (map.item != Items.MAP || !map.hoverName.string.contains("Magical Map")) return null
		return map
	}

	fun updateMapData(packet: ClientboundMapItemDataPacket) {
		if (!inDungeons) return
		MC.runOnThread {
			val map = getMapItem()
			map?.let {
				mapData = MapItem.getSavedData(it, MC.world)
			}

			if (mapData == null) {
				mapData = MapItem.getSavedData(packet.mapId, MC.world)
			}

			mapData?.let {
				packet.applyToMap(mapData)
				mapDataUpdated = true
			}
		}
	}

	/**
	 * Calibrates map metrics based on the size and location of the entrance room.
	 */
	fun calibrateMap(): Boolean {
		val (start, size) = findEntranceCorner()
		if (size.equalsOneOf(16, 18)) {
			roomSize = size
			halfRoomSize = roomSize / 2
			startCorner = when (DungeonUtils.currentFloor) {
				DungeonUtils.DungeonFloor.ENTRANCE -> Pair(22, 22)
				DungeonUtils.DungeonFloor.FLOOR_1, DungeonUtils.DungeonFloor.MASTER_MODE_FLOOR_1 -> Pair(22, 11)
				DungeonUtils.DungeonFloor.FLOOR_2, DungeonUtils.DungeonFloor.FLOOR_3 -> Pair(11, 11)

				else -> {
					val startX = start and 127
					val startZ = start shr 7
					Pair(startX % (roomSize + 4), startZ % (roomSize + 4))
				}
			}
			coordMultiplier = (roomSize + CONNECTOR_SIZE).toDouble() / DungeonScan.ROOM_SIZE
			return true
		}
		return false
	}

	/**
	 * Finds the starting index of the entrance room as well as the size of the room.
	 */
	private fun findEntranceCorner(): Pair<Int, Int> {
		var start = 0
		var currLength = 0
		mapData?.colors?.forEachIndexed { index, byte ->
			if (byte.toInt() == 30) {
				if (currLength == 0) start = index
				currLength++
			} else {
				if (currLength >= 16) {
					return Pair(start, currLength)
				}
				currLength = 0
			}
		}
		return Pair(start, currLength)
	}
}
