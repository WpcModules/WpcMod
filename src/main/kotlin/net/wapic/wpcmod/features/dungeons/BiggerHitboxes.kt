package net.wapic.wpcmod.features.dungeons

import net.minecraft.block.*
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.dungeon.DungeonConfig.InteractableBlocks
import net.wapic.wpcmod.util.DungeonUtils

object BiggerHitboxes {

	private val config get() = WpcMod.config.dungeon.hitboxes

	private val LEVER_MAP =
		VoxelShapes.createBlockFaceHorizontalFacingShapeMap(Block.createCuboidZShape(14.0, 14.0, 8.0, 16.0))
	private val BUTTON_MAP =
		VoxelShapes.createBlockFaceHorizontalFacingShapeMap(Block.createCuboidZShape(14.0, 14.0, 14.0, 16.0))
	private val SKULL_SHAPE = Block.createColumnShape(12.0, 0.0, 8.0)
	private val CHEST_SHAPE = Block.createColumnShape(16.0, 0.0, 16.0)

	fun getHitbox(blockState: BlockState): VoxelShape? {
		if (!DungeonUtils.inDungeons || !config.enabled) return null
		return when {
			blockState.block is LeverBlock && InteractableBlocks.LEVER in config.blocks -> {
				LEVER_MAP[blockState.get(WallMountedBlock.FACE)]?.get(blockState.get(HorizontalFacingBlock.FACING))
			}

			blockState.block is ButtonBlock && InteractableBlocks.BUTTON in config.blocks -> {
				BUTTON_MAP[blockState.get(WallMountedBlock.FACE)]?.get(blockState.get(HorizontalFacingBlock.FACING))
			}

			blockState.block is PlayerSkullBlock && InteractableBlocks.SKULL in config.blocks -> SKULL_SHAPE
			blockState.block is ChestBlock && InteractableBlocks.CHEST in config.blocks -> CHEST_SHAPE
			else -> null
		}
	}
}