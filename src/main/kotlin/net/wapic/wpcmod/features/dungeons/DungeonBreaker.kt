package net.wapic.wpcmod.features.dungeons

import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.minecraft.block.ButtonBlock
import net.minecraft.block.ChestBlock
import net.minecraft.block.LeverBlock
import net.minecraft.block.PlayerSkullBlock
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.dungeon.DungeonConfig.InteractableBlocks
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.ItemUtils.skyBlockID

object DungeonBreaker {

	private val config get() = WpcMod.config.dungeon
	private const val DUNGEON_BREAKER_ID = "DUNGEONBREAKER"

	fun init() {
		AttackBlockCallback.EVENT.register(::onAttackBlock)
	}

	private fun onAttackBlock(
		player: PlayerEntity,
		world: World,
		hand: Hand,
		pos: BlockPos,
		direction: Direction
	): ActionResult {
		if (!DungeonUtils.inDungeons) return ActionResult.PASS
		if (player.mainHandStack.skyBlockID != DUNGEON_BREAKER_ID) return ActionResult.PASS

		val block = world.getBlockState(pos).block
		val isPreventedBlock = when (block) {
			is ChestBlock -> InteractableBlocks.CHEST in config.preventedDungeonbreakerBlocks
			is ButtonBlock -> InteractableBlocks.BUTTON in config.preventedDungeonbreakerBlocks
			is LeverBlock -> InteractableBlocks.LEVER in config.preventedDungeonbreakerBlocks
			is PlayerSkullBlock -> InteractableBlocks.SKULL in config.preventedDungeonbreakerBlocks
			else -> false
		}

		if (isPreventedBlock) {
			ActionResult.FAIL
		}

		return ActionResult.PASS
	}
}