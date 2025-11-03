package net.wapic.wpcmod.features.dungeons.funnymap.dungeon

import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import net.minecraft.block.Blocks
import net.minecraft.block.entity.TrappedChestBlockEntity
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Heightmap
import net.wapic.wpcmod.features.dungeons.funnymap.core.RoomData
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.Room
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.Utils.equalsOneOf
import kotlin.math.roundToInt

object ScanUtils {

	var roomList: Set<RoomData> = setOf()

	init {
		try {
			roomList = Gson().fromJson(
				MC.resourceManager.getResourceOrThrow(Identifier.of("wpcmod", "rooms.json"))
				.inputStream.bufferedReader(),
				object : TypeToken<Set<RoomData>>() {}.type)
		} catch (e: JsonSyntaxException) {
			println("Error parsing FunnyMap room data.")
			e.printStackTrace()
		} catch (e: JsonIOException) {
			println("Error reading FunnyMap room data.")
			e.printStackTrace()
		}
	}

	fun findMimic(): String? {
		Utils.getLoadedBlockEntities().filterIsInstance<TrappedChestBlockEntity>()
			.groupingBy { getRoomFromPos(it.pos)?.data?.name }.eachCount()
			.forEach { (room, trappedChests) ->
				FunnyMap.Info.uniqueRooms.find { it.name == room && it.mainRoom.data.trappedChests < trappedChests }?.let {
					it.hasMimic = true
					return it.name
				}
			}
		return null
	}

	fun getRoomData(x: Int, z: Int): RoomData? {
		return getRoomData(getCore(x, z))
	}

	fun getRoomData(hash: Int): RoomData? {
		return roomList.find { hash in it.cores }
	}

	fun getRoomCentre(posX: Int, posZ: Int): Pair<Int, Int> {
		val roomX = ((posX - DungeonScan.START_X) / 32f).roundToInt()
		val roomZ = ((posZ - DungeonScan.START_Z) / 32f).roundToInt()
		return Pair(roomX * 32 + DungeonScan.START_X, roomZ * 32 + DungeonScan.START_Z)
	}

	fun getRoomFromPos(pos: BlockPos): Room? {
		val x = ((pos.x - DungeonScan.START_X + 15) shr 5)
		val z = ((pos.z - DungeonScan.START_Z + 15) shr 5)
		val room = FunnyMap.Info.dungeonList.getOrNull(x * 2 + z * 22)
		return room as? Room
	}

	fun getCore(x: Int, z: Int): Int {
		val sb = StringBuilder(150)
		val chunk = MC.world?.getChunk(x shr 4, z shr 4) ?: return 0
		val height = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, x, z).coerceIn(11..140)
		sb.append(CharArray(140 - height) { '0' })
		var bedrock = 0
		for (y in height downTo 12) {
			val block = chunk.getBlockState(BlockPos(x, y, z)).block
			val rawId = Item.getRawId(block.asItem())

			if (block == Blocks.AIR && bedrock >= 2 && y < 69) {
				sb.append(CharArray(y - 11) { '0' })
				break
			}

			if (block == Blocks.BEDROCK) {
				bedrock++
			} else {
				bedrock = 0
				if (block.equalsOneOf(
						Blocks.OAK_PLANKS,
						Blocks.SPRUCE_PLANKS,
						Blocks.BIRCH_PLANKS,
						Blocks.JUNGLE_PLANKS,
						Blocks.ACACIA_PLANKS,
						Blocks.DARK_OAK_PLANKS,
						Blocks.CHEST,
						Blocks.TRAPPED_CHEST
					)
				) {
					continue
				}
			}

			sb.append(rawId)
		}
		return sb.toString().hashCode()
	}
}
