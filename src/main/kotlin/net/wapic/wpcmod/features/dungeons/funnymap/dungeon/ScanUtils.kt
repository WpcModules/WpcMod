package net.wapic.wpcmod.features.dungeons.funnymap.dungeon

import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.SlabBlock
import net.minecraft.block.entity.TrappedChestBlockEntity
import net.minecraft.block.enums.SlabType
import net.minecraft.state.property.Properties
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

	val legacyIds = hashMapOf<Block, Int>(
		Blocks.AIR to 0,
		Blocks.STONE to 1,
		Blocks.ANDESITE to 1,
		Blocks.GRANITE to 1,
		Blocks.DIORITE to 1,
		Blocks.POLISHED_ANDESITE to 1,
		Blocks.POLISHED_DIORITE to 1,
		Blocks.POLISHED_GRANITE to 1,
		Blocks.GRASS_BLOCK to 2,
		Blocks.DIRT to 3,
		Blocks.COARSE_DIRT to 3,
		Blocks.PODZOL to 3,
		Blocks.COBBLESTONE to 4,
		Blocks.OAK_PLANKS to 5,
		Blocks.SPRUCE_PLANKS to 5,
		Blocks.BIRCH_PLANKS to 5,
		Blocks.JUNGLE_PLANKS to 5,
		Blocks.ACACIA_PLANKS to 5,
		Blocks.DARK_OAK_PLANKS to 5,
		Blocks.OAK_SAPLING to 6,
		Blocks.SPRUCE_SAPLING to 6,
		Blocks.BIRCH_SAPLING to 6,
		Blocks.JUNGLE_SAPLING to 6,
		Blocks.ACACIA_SAPLING to 6,
		Blocks.DARK_OAK_SAPLING to 6,
		Blocks.BEDROCK to 7,
		Blocks.WATER to 8,
		Blocks.WATER to 9,
		Blocks.LAVA to 10,
		Blocks.LAVA to 11,
		Blocks.SAND to 12,
		Blocks.RED_SAND to 12,
		Blocks.GRAVEL to 13,
		Blocks.GOLD_ORE to 14,
		Blocks.IRON_ORE to 15,
		Blocks.COAL_ORE to 16,
		Blocks.OAK_LOG to 17,
		Blocks.SPRUCE_LOG to 17,
		Blocks.BIRCH_LOG to 17,
		Blocks.JUNGLE_LOG to 17,
		Blocks.OAK_WOOD to 17,
		Blocks.SPRUCE_WOOD to 17,
		Blocks.BIRCH_WOOD to 17,
		Blocks.JUNGLE_WOOD to 17,
		Blocks.OAK_LEAVES to 18,
		Blocks.SPRUCE_LEAVES to 18,
		Blocks.BIRCH_LEAVES to 18,
		Blocks.JUNGLE_LEAVES to 18,
		Blocks.ACACIA_LEAVES to 18,
		Blocks.DARK_OAK_LEAVES to 18,
		Blocks.SPONGE to 19,
		Blocks.WET_SPONGE to 19,
		Blocks.GLASS to 20,
		Blocks.LAPIS_ORE to 21,
		Blocks.LAPIS_BLOCK to 22,
		Blocks.DISPENSER to 23,
		Blocks.SANDSTONE to 24,
		Blocks.CHISELED_SANDSTONE to 24,
		Blocks.SMOOTH_SANDSTONE to 24,
		Blocks.NOTE_BLOCK to 25,
		Blocks.WHITE_BED to 26,
		Blocks.POWERED_RAIL to 27,
		Blocks.DETECTOR_RAIL to 28,
		Blocks.STICKY_PISTON to 29,
		Blocks.COBWEB to 30,
		Blocks.FERN to 31,
		Blocks.SHORT_GRASS to 31,
		Blocks.DEAD_BUSH to 32,
		Blocks.PISTON to 33,
		Blocks.PISTON_HEAD to 34,
		Blocks.WHITE_WOOL to 35,
		Blocks.ORANGE_WOOL to 35,
		Blocks.MAGENTA_WOOL to 35,
		Blocks.LIGHT_BLUE_WOOL to 35,
		Blocks.YELLOW_WOOL to 35,
		Blocks.LIME_WOOL to 35,
		Blocks.PINK_WOOL to 35,
		Blocks.GRAY_WOOL to 35,
		Blocks.LIGHT_GRAY_WOOL to 35,
		Blocks.CYAN_WOOL to 35,
		Blocks.PURPLE_WOOL to 35,
		Blocks.BLUE_WOOL to 35,
		Blocks.BROWN_WOOL to 35,
		Blocks.GREEN_WOOL to 35,
		Blocks.RED_WOOL to 35,
		Blocks.BLACK_WOOL to 35,
		Blocks.DANDELION to 37,
		Blocks.POPPY to 38,
		Blocks.BLUE_ORCHID to 38,
		Blocks.ALLIUM to 38,
		Blocks.AZURE_BLUET to 38,
		Blocks.RED_TULIP to 38,
		Blocks.ORANGE_TULIP to 38,
		Blocks.WHITE_TULIP to 38,
		Blocks.PINK_TULIP to 38,
		Blocks.OXEYE_DAISY to 38,
		Blocks.BROWN_MUSHROOM to 39,
		Blocks.RED_MUSHROOM to 40,
		Blocks.GOLD_BLOCK to 41,
		Blocks.IRON_BLOCK to 42,
		Blocks.SMOOTH_STONE_SLAB to 44,
		Blocks.STONE_SLAB to 44,
		Blocks.SANDSTONE_SLAB to 44,
		Blocks.OAK_SLAB to 44,
		Blocks.COBBLESTONE_SLAB to 44,
		Blocks.STONE_BRICK_SLAB to 44,
		Blocks.NETHER_BRICK_SLAB to 44,
		Blocks.QUARTZ_SLAB to 44,
		Blocks.BRICKS to 45,
		Blocks.TNT to 46,
		Blocks.BOOKSHELF to 47,
		Blocks.MOSSY_COBBLESTONE to 48,
		Blocks.OBSIDIAN to 49,
		Blocks.TORCH to 50,
		Blocks.FIRE to 51,
		Blocks.SPAWNER to 52,
		Blocks.OAK_STAIRS to 53,
		Blocks.CHEST to 54,
		Blocks.REDSTONE_WIRE to 55,
		Blocks.DIAMOND_ORE to 56,
		Blocks.DIAMOND_BLOCK to 57,
		Blocks.CRAFTING_TABLE to 58,
		Blocks.WHEAT to 59,
		Blocks.FARMLAND to 60,
		Blocks.FURNACE to 61,
		Blocks.FURNACE to 62,
		Blocks.OAK_SIGN to 63,
		Blocks.OAK_DOOR to 64,
		Blocks.LADDER to 65,
		Blocks.RAIL to 66,
		Blocks.STONE_STAIRS to 67,
		Blocks.COBBLESTONE_STAIRS to 67,
		Blocks.OAK_WALL_SIGN to 68,
		Blocks.LEVER to 69,
		Blocks.STONE_PRESSURE_PLATE to 70,
		Blocks.IRON_DOOR to 71,
		Blocks.OAK_PRESSURE_PLATE to 72,
		Blocks.REDSTONE_ORE to 73,
		Blocks.REDSTONE_ORE to 74,
		Blocks.REDSTONE_TORCH to 75,
		Blocks.REDSTONE_TORCH to 76,
		Blocks.STONE_BUTTON to 77,
		Blocks.SNOW to 78,
		Blocks.ICE to 79,
		Blocks.SNOW_BLOCK to 80,
		Blocks.CACTUS to 81,
		Blocks.CLAY to 82,
		Blocks.SUGAR_CANE to 83,
		Blocks.JUKEBOX to 84,
		Blocks.OAK_FENCE to 85,
		Blocks.SPRUCE_FENCE to 85,
		Blocks.BIRCH_FENCE to 85,
		Blocks.JUNGLE_FENCE to 85,
		Blocks.ACACIA_FENCE to 85,
		Blocks.DARK_OAK_FENCE to 85,
		Blocks.PUMPKIN to 86,
		Blocks.NETHERRACK to 87,
		Blocks.SOUL_SAND to 88,
		Blocks.GLOWSTONE to 89,
		Blocks.NETHER_PORTAL to 90,
		Blocks.JACK_O_LANTERN to 91,
		Blocks.CAKE to 92,
		Blocks.REPEATER to 93,
		Blocks.REPEATER to 94,
		Blocks.WHITE_STAINED_GLASS to 95,
		Blocks.ORANGE_STAINED_GLASS to 95,
		Blocks.MAGENTA_STAINED_GLASS to 95,
		Blocks.LIGHT_BLUE_STAINED_GLASS to 95,
		Blocks.YELLOW_STAINED_GLASS to 95,
		Blocks.LIME_STAINED_GLASS to 95,
		Blocks.PINK_STAINED_GLASS to 95,
		Blocks.GRAY_STAINED_GLASS to 95,
		Blocks.LIGHT_GRAY_STAINED_GLASS to 95,
		Blocks.CYAN_STAINED_GLASS to 95,
		Blocks.PURPLE_STAINED_GLASS to 95,
		Blocks.BLUE_STAINED_GLASS to 95,
		Blocks.BROWN_STAINED_GLASS to 95,
		Blocks.GREEN_STAINED_GLASS to 95,
		Blocks.RED_STAINED_GLASS to 95,
		Blocks.BLACK_STAINED_GLASS to 95,
		Blocks.OAK_TRAPDOOR to 96,
		Blocks.INFESTED_STONE to 97,
		Blocks.INFESTED_COBBLESTONE to 97,
		Blocks.INFESTED_STONE_BRICKS to 97,
		Blocks.INFESTED_MOSSY_STONE_BRICKS to 97,
		Blocks.INFESTED_CRACKED_STONE_BRICKS to 97,
		Blocks.INFESTED_CHISELED_STONE_BRICKS to 97,
		Blocks.STONE_BRICKS to 98,
		Blocks.MOSSY_STONE_BRICKS to 98,
		Blocks.CRACKED_STONE_BRICKS to 98,
		Blocks.CHISELED_STONE_BRICKS to 98,
		Blocks.BROWN_MUSHROOM_BLOCK to 99,
		Blocks.RED_MUSHROOM_BLOCK to 100,
		Blocks.IRON_BARS to 101,
		Blocks.GLASS_PANE to 102,
		Blocks.MELON to 103,
		Blocks.PUMPKIN_STEM to 104,
		Blocks.MELON_STEM to 105,
		Blocks.VINE to 106,
		Blocks.OAK_FENCE_GATE to 107,
		Blocks.SPRUCE_FENCE_GATE to 107,
		Blocks.BIRCH_FENCE_GATE to 107,
		Blocks.JUNGLE_FENCE_GATE to 107,
		Blocks.ACACIA_FENCE_GATE to 107,
		Blocks.DARK_OAK_FENCE_GATE to 107,
		Blocks.BRICK_STAIRS to 108,
		Blocks.STONE_BRICK_STAIRS to 109,
		Blocks.MYCELIUM to 110,
		Blocks.LILY_PAD to 111,
		Blocks.NETHER_BRICKS to 112,
		Blocks.NETHER_BRICK_FENCE to 113,
		Blocks.NETHER_BRICK_STAIRS to 114,
		Blocks.NETHER_WART to 115,
		Blocks.ENCHANTING_TABLE to 116,
		Blocks.BREWING_STAND to 117,
		Blocks.CAULDRON to 118,
		Blocks.END_PORTAL to 119,
		Blocks.END_PORTAL_FRAME to 120,
		Blocks.END_STONE to 121,
		Blocks.DRAGON_EGG to 122,
		Blocks.REDSTONE_LAMP to 123,
		Blocks.REDSTONE_LAMP to 124,
		Blocks.OAK_SLAB to 126,
		Blocks.SPRUCE_SLAB to 126,
		Blocks.BIRCH_SLAB to 126,
		Blocks.JUNGLE_SLAB to 126,
		Blocks.ACACIA_SLAB to 126,
		Blocks.DARK_OAK_SLAB to 126,
		Blocks.COCOA to 127,
		Blocks.SANDSTONE_STAIRS to 128,
		Blocks.EMERALD_ORE to 129,
		Blocks.ENDER_CHEST to 130,
		Blocks.TRIPWIRE_HOOK to 131,
		Blocks.TRIPWIRE to 132,
		Blocks.EMERALD_BLOCK to 133,
		Blocks.SPRUCE_STAIRS to 134,
		Blocks.BIRCH_STAIRS to 135,
		Blocks.JUNGLE_STAIRS to 136,
		Blocks.COMMAND_BLOCK to 137,
		Blocks.BEACON to 138,
		Blocks.COBBLESTONE_WALL to 139,
		Blocks.MOSSY_COBBLESTONE_WALL to 139,
		Blocks.FLOWER_POT to 140,
		Blocks.POTTED_POPPY to 140,
		Blocks.POTTED_DANDELION to 140,
		Blocks.POTTED_OAK_SAPLING to 140,
		Blocks.POTTED_SPRUCE_SAPLING to 140,
		Blocks.POTTED_BIRCH_SAPLING to 140,
		Blocks.POTTED_JUNGLE_SAPLING to 140,
		Blocks.POTTED_RED_MUSHROOM to 140,
		Blocks.POTTED_BROWN_MUSHROOM to 140,
		Blocks.POTTED_CACTUS to 140,
		Blocks.POTTED_DEAD_BUSH to 140,
		Blocks.POTTED_FERN to 140,
		Blocks.POTTED_ACACIA_SAPLING to 140,
		Blocks.POTTED_DARK_OAK_SAPLING to 140,
		Blocks.CARROTS to 141,
		Blocks.POTATOES to 142,
		Blocks.OAK_BUTTON to 143,
		Blocks.SKELETON_SKULL to 144,
		Blocks.WITHER_SKELETON_SKULL to 144,
		Blocks.ZOMBIE_HEAD to 144,
		Blocks.PLAYER_HEAD to 144,
		Blocks.CREEPER_HEAD to 144,
		Blocks.DRAGON_HEAD to 144,
		Blocks.ANVIL to 145,
		Blocks.TRAPPED_CHEST to 146,
		Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE to 147,
		Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE to 148,
		Blocks.COMPARATOR to 149,
		Blocks.COMPARATOR to 150,
		Blocks.DAYLIGHT_DETECTOR to 151,
		Blocks.REDSTONE_BLOCK to 152,
		Blocks.NETHER_QUARTZ_ORE to 153,
		Blocks.HOPPER to 154,
		Blocks.QUARTZ_BLOCK to 155,
		Blocks.CHISELED_QUARTZ_BLOCK to 155,
		Blocks.QUARTZ_PILLAR to 155,
		Blocks.QUARTZ_STAIRS to 156,
		Blocks.ACTIVATOR_RAIL to 157,
		Blocks.DROPPER to 158,
		Blocks.WHITE_TERRACOTTA to 159,
		Blocks.ORANGE_TERRACOTTA to 159,
		Blocks.MAGENTA_TERRACOTTA to 159,
		Blocks.LIGHT_BLUE_TERRACOTTA to 159,
		Blocks.YELLOW_TERRACOTTA to 159,
		Blocks.LIME_TERRACOTTA to 159,
		Blocks.PINK_TERRACOTTA to 159,
		Blocks.GRAY_TERRACOTTA to 159,
		Blocks.LIGHT_GRAY_TERRACOTTA to 159,
		Blocks.CYAN_TERRACOTTA to 159,
		Blocks.PURPLE_TERRACOTTA to 159,
		Blocks.BLUE_TERRACOTTA to 159,
		Blocks.BROWN_TERRACOTTA to 159,
		Blocks.GREEN_TERRACOTTA to 159,
		Blocks.RED_TERRACOTTA to 159,
		Blocks.BLACK_TERRACOTTA to 159,
		Blocks.WHITE_STAINED_GLASS_PANE to 160,
		Blocks.ORANGE_STAINED_GLASS_PANE to 160,
		Blocks.MAGENTA_STAINED_GLASS_PANE to 160,
		Blocks.LIGHT_BLUE_STAINED_GLASS_PANE to 160,
		Blocks.YELLOW_STAINED_GLASS_PANE to 160,
		Blocks.LIME_STAINED_GLASS_PANE to 160,
		Blocks.PINK_STAINED_GLASS_PANE to 160,
		Blocks.GRAY_STAINED_GLASS_PANE to 160,
		Blocks.LIGHT_GRAY_STAINED_GLASS_PANE to 160,
		Blocks.CYAN_STAINED_GLASS_PANE to 160,
		Blocks.PURPLE_STAINED_GLASS_PANE to 160,
		Blocks.BLUE_STAINED_GLASS_PANE to 160,
		Blocks.BROWN_STAINED_GLASS_PANE to 160,
		Blocks.GREEN_STAINED_GLASS_PANE to 160,
		Blocks.RED_STAINED_GLASS_PANE to 160,
		Blocks.BLACK_STAINED_GLASS_PANE to 160,
		Blocks.ACACIA_LEAVES to 161,
		Blocks.DARK_OAK_LEAVES to 161,
		Blocks.ACACIA_LOG to 162,
		Blocks.DARK_OAK_LOG to 162,
		Blocks.ACACIA_STAIRS to 163,
		Blocks.DARK_OAK_STAIRS to 164,
		Blocks.SLIME_BLOCK to 165,
		Blocks.BARRIER to 166,
		Blocks.IRON_TRAPDOOR to 167,
		Blocks.PRISMARINE to 168,
		Blocks.PRISMARINE_BRICKS to 168,
		Blocks.DARK_PRISMARINE to 168,
		Blocks.SEA_LANTERN to 169,
		Blocks.HAY_BLOCK to 170,
		Blocks.WHITE_CARPET to 171,
		Blocks.ORANGE_CARPET to 171,
		Blocks.MAGENTA_CARPET to 171,
		Blocks.LIGHT_BLUE_CARPET to 171,
		Blocks.YELLOW_CARPET to 171,
		Blocks.LIME_CARPET to 171,
		Blocks.PINK_CARPET to 171,
		Blocks.GRAY_CARPET to 171,
		Blocks.LIGHT_GRAY_CARPET to 171,
		Blocks.CYAN_CARPET to 171,
		Blocks.PURPLE_CARPET to 171,
		Blocks.BLUE_CARPET to 171,
		Blocks.BROWN_CARPET to 171,
		Blocks.GREEN_CARPET to 171,
		Blocks.RED_CARPET to 171,
		Blocks.BLACK_CARPET to 171,
		Blocks.TERRACOTTA to 172,
		Blocks.COAL_BLOCK to 173,
		Blocks.PACKED_ICE to 174,
		Blocks.LARGE_FERN to 175,
		Blocks.SUNFLOWER to 175,
		Blocks.ROSE_BUSH to 175,
		Blocks.PEONY to 175,
		Blocks.LILAC to 175,
		Blocks.TALL_GRASS to 175,
		Blocks.WHITE_BANNER to 176,
		Blocks.ORANGE_BANNER to 176,
		Blocks.MAGENTA_BANNER to 176,
		Blocks.LIGHT_BLUE_BANNER to 176,
		Blocks.YELLOW_BANNER to 176,
		Blocks.LIME_BANNER to 176,
		Blocks.PINK_BANNER to 176,
		Blocks.GRAY_BANNER to 176,
		Blocks.LIGHT_GRAY_BANNER to 176,
		Blocks.CYAN_BANNER to 176,
		Blocks.PURPLE_BANNER to 176,
		Blocks.BLUE_BANNER to 176,
		Blocks.BROWN_BANNER to 176,
		Blocks.GREEN_BANNER to 176,
		Blocks.RED_BANNER to 176,
		Blocks.BLACK_BANNER to 176,
		Blocks.WHITE_WALL_BANNER to 177,
		Blocks.ORANGE_WALL_BANNER to 177,
		Blocks.MAGENTA_WALL_BANNER to 177,
		Blocks.LIGHT_BLUE_WALL_BANNER to 177,
		Blocks.YELLOW_WALL_BANNER to 177,
		Blocks.LIME_WALL_BANNER to 177,
		Blocks.PINK_WALL_BANNER to 177,
		Blocks.GRAY_WALL_BANNER to 177,
		Blocks.LIGHT_GRAY_WALL_BANNER to 177,
		Blocks.CYAN_WALL_BANNER to 177,
		Blocks.PURPLE_WALL_BANNER to 177,
		Blocks.BLUE_WALL_BANNER to 177,
		Blocks.BROWN_WALL_BANNER to 177,
		Blocks.GREEN_WALL_BANNER to 177,
		Blocks.RED_WALL_BANNER to 177,
		Blocks.BLACK_WALL_BANNER to 177,
		Blocks.DAYLIGHT_DETECTOR to 178,
		Blocks.RED_SANDSTONE to 179,
		Blocks.CHISELED_RED_SANDSTONE to 179,
		Blocks.SMOOTH_RED_SANDSTONE to 179,
		Blocks.RED_SANDSTONE_STAIRS to 180,
		Blocks.RED_SANDSTONE_SLAB to 182,
		Blocks.SPRUCE_FENCE_GATE to 183,
		Blocks.BIRCH_FENCE_GATE to 184,
		Blocks.JUNGLE_FENCE_GATE to 185,
		Blocks.DARK_OAK_FENCE_GATE to 186,
		Blocks.ACACIA_FENCE_GATE to 187,
		Blocks.SPRUCE_FENCE to 188,
		Blocks.BIRCH_FENCE to 189,
		Blocks.JUNGLE_FENCE to 190,
		Blocks.DARK_OAK_FENCE to 191,
		Blocks.ACACIA_FENCE to 192,
		Blocks.SPRUCE_DOOR to 193,
		Blocks.BIRCH_DOOR to 194,
		Blocks.JUNGLE_DOOR to 195,
		Blocks.ACACIA_DOOR to 196,
		Blocks.DARK_OAK_DOOR to 197,
	)

	var roomList: Set<RoomData> = setOf()

	init {
		try {
			roomList = Gson().fromJson(
				MC.resourceManager.getResourceOrThrow(Identifier.of("wpcmod", "rooms-legacy.json"))
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
			val block = chunk.getBlockState(BlockPos(x, y, z))
			var id = legacyIds[block.block]
			if (block.block is SlabBlock && block.get(Properties.SLAB_TYPE) == SlabType.DOUBLE) {
				id = id?.minus(1)
			}
			if (id == 0 && bedrock >= 2 && y < 69) {
				sb.append(CharArray(y - 11) { '0' })
				break
			}

			if (id == 7) {
				bedrock++
			} else {
				bedrock = 0
				if (id.equalsOneOf(5, 54, 146)) {
					continue
				}
			}

			sb.append(id)
		}
		return sb.toString().hashCode()
	}

}
