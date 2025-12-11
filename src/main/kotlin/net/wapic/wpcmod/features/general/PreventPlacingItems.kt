package net.wapic.wpcmod.features.general

import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.entity.player.Player
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionHand
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.level.Level
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.ItemUtils.skyBlockID
import net.wapic.wpcmod.util.MC

object PreventPlacingItems {

	private val config get() = WpcMod.config.general

	private val placeableItems = listOf(
		"FLOWER_OF_TRUTH",
		"BOUQUET_OF_LIES",
		"MOODY_GRAPPLESHOT",
		"BAT_WAND",
		"STARRED_BAT_WAND",
		"WEIRD_TUBA",
		"WEIRDER_TUBA",
		"PUMPKIN_LAUNCHER",
		"FIRE_FREEZE_STAFF",
		"BASIC_FISHING_NET",
		"MEDIUM_FISHING_NET",
		"TURBO_FISHING_NET",
		"RADIANT_POWER_ORB",
		"MANAFLUX_POWER_ORB",
		"OVERFLUX_POWER_ORB",
		"PLASMAFLUX_POWER_ORB",
	)

	private val interactables = setOf(
		Blocks.ACACIA_DOOR,
		Blocks.ANVIL,
		Blocks.BEACON,
		Blocks.BIRCH_DOOR,
		Blocks.BREWING_STAND,
		Blocks.COMMAND_BLOCK,
		Blocks.CRAFTING_TABLE,
		Blocks.CHEST,
		Blocks.TRAPPED_CHEST,
		Blocks.DARK_OAK_DOOR,
		Blocks.DAYLIGHT_DETECTOR,
		Blocks.DISPENSER,
		Blocks.DROPPER,
		Blocks.ENCHANTING_TABLE,
		Blocks.ENDER_CHEST,
		Blocks.FURNACE,
		Blocks.HOPPER,
		Blocks.JUNGLE_DOOR,
		Blocks.LEVER,
		Blocks.NOTE_BLOCK,
		Blocks.COMPARATOR,
		Blocks.REPEATER,
		Blocks.TRAPPED_CHEST,
		Blocks.STONE_BUTTON,
		Blocks.OAK_BUTTON,
		Blocks.OAK_DOOR,
		Blocks.WITHER_SKELETON_SKULL,
		Blocks.SKELETON_SKULL,
		Blocks.PLAYER_HEAD,
		Blocks.OAK_TRAPDOOR,
		Blocks.OAK_SIGN,
	)

	fun init() {
		UseBlockCallback.EVENT.register(::onUseBlock)
	}

	private fun onUseBlock(player: Player, world: Level, hand: InteractionHand, hitResult: BlockHitResult): InteractionResult {
		if (!config.preventPlacing || player.mainHandItem.isEmpty) return InteractionResult.PASS
		val item = player.mainHandItem.skyBlockID ?: return InteractionResult.PASS

		if (item.contains("ABIPHONE") || item in placeableItems) {

			val block = MC.world?.getBlockState(hitResult.blockPos)
			if (block?.block in interactables || DungeonUtils.inDungeons && (block?.block == Blocks.COAL_BLOCK || block?.block == Blocks.RED_TERRACOTTA)) {
				return InteractionResult.PASS
			}

			return InteractionResult.SUCCESS
		}

		return InteractionResult.PASS
	}
}