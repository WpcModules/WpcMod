package net.wapic.wpcmod.features.dungeons

import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.dungeon.DungeonConfig.InteractableBlocks
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.skyblockId

object DungeonBreaker {

	private val config get() = WpcMod.config.dungeon.dungeonbreaker
	private const val DUNGEON_BREAKER_ID = "DUNGEONBREAKER"
	private val blacklistedBlocks = listOf(Blocks.BEDROCK, Blocks.END_PORTAL_FRAME)

	fun init() {
		AttackBlockCallback.EVENT.register(::onAttackBlock)
	}

	private fun onAttackBlock(
		player: Player,
		level: Level,
		hand: InteractionHand,
		pos: BlockPos,
		direction: Direction
	): InteractionResult {
		if (!DungeonUtils.inDungeons) return InteractionResult.PASS
		if (player.mainHandItem.skyblockId != DUNGEON_BREAKER_ID) return InteractionResult.PASS

		val block = level.getBlockState(pos).block
		val isPreventedBlock = when (block) {
			is ChestBlock -> InteractableBlocks.CHEST in config.preventedDungeonbreakerBlocks
			is ButtonBlock -> InteractableBlocks.BUTTON in config.preventedDungeonbreakerBlocks
			is LeverBlock -> InteractableBlocks.LEVER in config.preventedDungeonbreakerBlocks
			is PlayerHeadBlock -> InteractableBlocks.SKULL in config.preventedDungeonbreakerBlocks
			else -> false
		}

		val disableInTicTacToe = config.fuckTicTacToe && DungeonUtils.currentRoom == "Tic Tac Toe"
		val shouldRemoveBlock = config.zeroPingDB && !isPreventedBlock && block !in blacklistedBlocks
		if (shouldRemoveBlock && !disableInTicTacToe) {
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3)
		}

		if (isPreventedBlock && config.preventBreakingSecrets) {
			return InteractionResult.FAIL
		}

		return InteractionResult.PASS
	}
}