package net.wapic.wpcmod.features.dungeons.funnymap.dungeon

import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Heightmap
import net.minecraft.world.chunk.EmptyChunk
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.dungeons.ScoreCalculation
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.*
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.DungeonScan.scan
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.equalsOneOf

/**
 * Handles everything related to scanning the dungeon. Running [scan] will update the instance of [FunnyMap].
 */
object DungeonScan {

	private val config get() = WpcMod.config.dungeon.funnyMap

	/**
	 * The size of each dungeon room in blocks.
	 */
	const val ROOM_SIZE = 32

	/**
	 * The starting coordinates to start scanning (the north-west corner).
	 */
	const val START_X = -185
	const val START_Z = -185

	private var lastScanTime = 0L
	var isScanning = false
	var hasScanned = false

	val shouldScan: Boolean
		get() = config.autoScan && !isScanning && !hasScanned && System.currentTimeMillis() - lastScanTime >= 250 && DungeonUtils.currentFloor != DungeonUtils.DungeonFloor.NONE

	fun scan() {
		isScanning = true
		var allChunksLoaded = true

		// Scans the dungeon in a 11x11 grid.
		for (x in 0..10) {
			for (z in 0..10) {
				// Translates the grid index into world position.
				val xPos = START_X + x * (ROOM_SIZE shr 1)
				val zPos = START_Z + z * (ROOM_SIZE shr 1)

				if (MC.world?.getChunk(xPos shr 4, zPos shr 4) is EmptyChunk) {
					allChunksLoaded = false
					continue
				}

				val hasBeenScanned =
					FunnyMap.Info.dungeonList[x + z * 11].run { this !is Unknown && (this as? Room)?.data?.name != "Unknown" }
				if (hasBeenScanned) continue

				scanRoom(xPos, zPos, z, x)?.let {
					val prev = FunnyMap.Info.dungeonList[z * 11 + x]
					if (it is Room) {
						if ((prev as? Room)?.uniqueRoom != null) {
							prev.uniqueRoom?.addTile(x, z, it)
						} else if (FunnyMap.Info.uniqueRooms.none { unique -> unique.name == it.data.name }) {
							UniqueRoom(x, z, it)
						}
						MapUpdate.roomAdded = true
						WpcMod.logger.debug("{} room with type {} was added", it.data.name, it.data.type)
					}
					FunnyMap.Info.dungeonList[x + z * 11] = it
				}
			}
		}

		if (MapUpdate.roomAdded) {
			MapUpdate.updateUniques()
		}

		if (allChunksLoaded) {
			if (config.scanChatInfo) {
				val lines = mutableListOf(
					"§aScan Finished!",
					"§aPuzzles (§c${FunnyMap.Info.puzzles.size}§a):",
					FunnyMap.Info.puzzles.entries.joinToString(
						separator = "\n§b- §d",
						prefix = "§b- §d"
					) { it.key.roomDataName },
					"§6Trap: §a${FunnyMap.Info.trapType}",
					"§8Wither Doors: §7${FunnyMap.Info.witherDoors - 1}",
					"§7Total Crypts: §6${FunnyMap.Info.cryptCount}",
					"§7Total Secrets: §b${FunnyMap.Info.secretCount}",
				)
				ChatUtils.sendMessage(lines.joinToString(separator = "\n"))
			}
			FunnyMap.Info.roomCount = FunnyMap.Info.dungeonList.filter { it is Room && !it.isSeparator }.size

			ScoreCalculation.totalSecrets = FunnyMap.Info.secretCount

			hasScanned = true
		}

		lastScanTime = System.currentTimeMillis()
		isScanning = false
	}

	private fun scanRoom(x: Int, z: Int, row: Int, column: Int): Tile? {
		val height = MC.world?.getChunk(x shr 4, z shr 4)?.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, x, z)
		if (height == -1) return null

		val rowEven = row and 1 == 0
		val columnEven = column and 1 == 0

		return when {
			// Scanning a room
			rowEven && columnEven -> {
				val roomCore = ScanUtils.getCore(x, z)
				Room(x, z, ScanUtils.getRoomData(roomCore) ?: return null).apply { core = roomCore }
			}

			// Can only be the center "block" of a 2x2 room.
			!rowEven && !columnEven -> {
				FunnyMap.Info.dungeonList[column - 1 + (row - 1) * 11].let {
					if (it is Room) {
						Room(x, z, it.data).apply { isSeparator = true }
					} else {
						null
					}
				}
			}

			// Doorway between rooms
			// Old trap has a single block at 81
			height.equalsOneOf(73, 81) -> {
				Door(
					x, z,
					// Finds door type from door block
					type = when (MC.world?.getBlockState(BlockPos(x, 69, z))?.block) {
						Blocks.COAL_BLOCK -> {
							FunnyMap.Info.witherDoors++
							DoorType.WITHER
						}

						Blocks.INFESTED_CHISELED_STONE_BRICKS -> DoorType.ENTRANCE
						Blocks.RED_TERRACOTTA -> DoorType.BLOOD
						else -> DoorType.NORMAL
					}
				)
			}

			// Connection between large rooms
			else -> {
				FunnyMap.Info.dungeonList[if (rowEven) row * 11 + column - 1 else (row - 1) * 11 + column].let {
					if (it !is Room) {
						null
					} else if (it.data.type == RoomType.ENTRANCE) {
						Door(x, z, DoorType.ENTRANCE)
					} else {
						Room(x, z, it.data).apply { isSeparator = true }
					}
				}
			}
		}
	}
}
