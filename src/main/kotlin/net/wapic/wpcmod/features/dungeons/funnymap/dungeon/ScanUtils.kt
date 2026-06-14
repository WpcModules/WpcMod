package net.wapic.wpcmod.features.dungeons.funnymap.dungeon

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity
import net.minecraft.world.level.levelgen.Heightmap
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.dungeons.funnymap.core.RoomData
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.Room
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.FileManager
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.Utils.equalsOneOf
import java.io.File
import kotlin.math.roundToInt

object ScanUtils {

	var roomList: Set<RoomData> = setOf()
	val roomsFile = File(WpcMod.configDir, "rooms-new.json")
	val backupFile = File(roomsFile.parentFile, "${roomsFile.name}.bak")
	val gson: Gson = GsonBuilder().setPrettyPrinting().create()

	init {
		try {
			roomList = gson.fromJson(
				MC.resourceManager.getResourceOrThrow(Identifier.fromNamespaceAndPath("wpcmod", "dungeon/rooms.json"))
				.open().bufferedReader(),
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
			.groupingBy { getRoomFromPos(it.blockPos)?.data?.name }.eachCount()
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

	fun getRoomDataFromName(roomName: String): RoomData? {
		return roomList.find { it.name.equals(roomName, true) }
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

	fun addCore(roomName: String) {
		val data = getRoomDataFromName(roomName)
		if (data != null) {
			val pos = MC.cameraPos ?: return
			val roomCentre = getRoomCentre(pos.x.toInt(), pos.z.toInt())
			val newCore = getCore(roomCentre.first, roomCentre.second)
			WpcMod.LOGGER.debug("Adding {} to room {}", newCore, data)

			val newCores = data.cores + newCore
			if (newCore in data.cores) return WpcMod.LOGGER.warn("Core already exists in room ${data.name}")

			roomList = roomList.map { roomData ->
				return@map if (roomData.name == data.name) RoomData(
					data.name,
					data.type,
					newCores,
					data.crypts,
					data.secrets,
					data.trappedChests
				) else roomData
			}.toSet()

			ChatUtils.sendMessage("Added core: $newCore to \"$roomName\"", Style.EMPTY.withColor(ChatFormatting.GREEN))
		} else {
			ChatUtils.sendMessage("Unable to find room from \"$roomName\"", Style.EMPTY.withColor(ChatFormatting.RED))
		}
	}

	fun saveRoomList() {
		val json = gson.toJson(roomList, object : TypeToken<Set<RoomData>>() {}.type)
		FileManager.saveFile(json, roomsFile, backupFile)
	}

	fun getCore(x: Int, z: Int): Int {
		val sb = StringBuilder(150)
		val chunk = MC.level?.getChunk(x shr 4, z shr 4) ?: return 0
		val height = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z).coerceIn(11..140)
		sb.append(CharArray(140 - height) { '0' })
		var bedrock = 0
		for (y in height downTo 12) {
			val block = chunk.getBlockState(BlockPos(x, y, z)).block
			val rawId = BuiltInRegistries.BLOCK.getKey(block).toLanguageKey().hashCode()

			if (block == Blocks.AIR && bedrock >= 2 && y < 69) {
				sb.append(CharArray(y - 11) { '0' })
				break
			}

			if (block == Blocks.BEDROCK) {
				bedrock++
			} else {
				bedrock = 0
				if (block.equalsOneOf(Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.LEVER)) {
					continue
				}
			}

			sb.append(rawId)
		}
		return sb.toString().hashCode()
	}
}
