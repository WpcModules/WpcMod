package net.wapic.wpcmod.features.dungeons

import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.minecraft.block.Blocks
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.ItemUtils.skyBlockID
import net.wapic.wpcmod.util.Utils.equalsOneOf

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
		if (!DungeonUtils.inDungeons || !config.preventBreakingChests) return ActionResult.PASS

		val isHoldingDungeonBreaker = player.mainHandStack.skyBlockID == DUNGEON_BREAKER_ID
		val isChestBlock = world.getBlockState(pos).block.equalsOneOf(Blocks.CHEST, Blocks.TRAPPED_CHEST)
		return if (isHoldingDungeonBreaker && isChestBlock) ActionResult.FAIL else ActionResult.PASS
	}
}