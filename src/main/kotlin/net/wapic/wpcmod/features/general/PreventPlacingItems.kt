package net.wapic.wpcmod.features.general

import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.world.World
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.ItemUtils.getSkyBlockID
import net.wapic.wpcmod.util.Utils

class PreventPlacingItems {
    private val config get() = WpcMod.config.generalConfig

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

    val interactables = setOf(
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

    init {
        UseBlockCallback.EVENT.register(::onUseBlock)
    }

    fun onUseBlock(player: PlayerEntity, world: World, hand: Hand, hitResult: BlockHitResult): ActionResult {
        if(!config.preventPlacing || player.mainHandStack.isEmpty) return ActionResult.PASS
        val item = player.mainHandStack.getSkyBlockID() ?: return ActionResult.PASS

        if(item.contains("ABIPHONE".toRegex()) || item in placeableItems) {

            val block = MinecraftClient.getInstance().world?.getBlockState(hitResult.blockPos)
            if(block?.block in interactables || Utils.getLocation() == Island.DUNGEON && (block?.block == Blocks.COAL_BLOCK || block?.block == Blocks.RED_TERRACOTTA)) {
                return ActionResult.PASS
            }

            return ActionResult.SUCCESS
        }

        return ActionResult.PASS
    }
}