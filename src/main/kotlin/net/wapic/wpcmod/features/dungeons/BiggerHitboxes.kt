package net.wapic.wpcmod.features.dungeons

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.ButtonBlock
import net.minecraft.world.level.block.ChestBlock
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.LeverBlock
import net.minecraft.world.level.block.PlayerHeadBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.phys.shapes.Shapes
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.dungeon.DungeonConfig.InteractableBlocks
import net.wapic.wpcmod.util.DungeonUtils

object BiggerHitboxes {

	private val config get() = WpcMod.config.dungeon.hitboxes

	private val LEVER_MAP =
		Shapes.rotateAttachFace(Block.boxZ(14.0, 14.0, 8.0, 16.0))
	private val BUTTON_MAP =
		Shapes.rotateAttachFace(Block.boxZ(14.0, 14.0, 14.0, 16.0))
	private val SKULL_SHAPE = Block.column(16.0, 0.0, 16.0)
	private val CHEST_SHAPE = Shapes.block()

	fun getHitbox(blockState: BlockState): VoxelShape? {
		if (!DungeonUtils.inDungeons || !config.enabled) return null
		return when {
			blockState.block is LeverBlock && InteractableBlocks.LEVER in config.blocks -> {
				LEVER_MAP[blockState.getValue(FaceAttachedHorizontalDirectionalBlock.FACE)]
					?.get(blockState.getValue(HorizontalDirectionalBlock.FACING))
			}

			blockState.block is ButtonBlock && InteractableBlocks.BUTTON in config.blocks -> {
				BUTTON_MAP[blockState.getValue(FaceAttachedHorizontalDirectionalBlock.FACE)]
					?.get(blockState.getValue(HorizontalDirectionalBlock.FACING))
			}

			blockState.block is PlayerHeadBlock && InteractableBlocks.SKULL in config.blocks -> SKULL_SHAPE
			blockState.block is ChestBlock && InteractableBlocks.CHEST in config.blocks -> CHEST_SHAPE
			else -> null
		}
	}
}