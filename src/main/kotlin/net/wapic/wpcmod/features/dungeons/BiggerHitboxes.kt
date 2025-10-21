package net.wapic.wpcmod.features.dungeons

import net.minecraft.block.Block
import net.minecraft.block.enums.BlockFace
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape

object BiggerHitboxes {

	val LEVER_SHAPES = hashMapOf(
		BlockFace.FLOOR to Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 10.0, 14.0),
		BlockFace.CEILING to Block.createCuboidShape(2.0, 6.0, 2.0, 14.0, 16.0, 14.0),
		Direction.NORTH to Block.createCuboidShape(2.0, 2.0, 6.0, 14.0, 14.0, 16.0),
		Direction.SOUTH to Block.createCuboidShape(2.0, 2.0, 0.0, 14.0, 14.0, 10.0),
		Direction.WEST to Block.createCuboidShape(6.0, 2.0, 2.0, 16.0, 14.0, 14.0),
		Direction.EAST to Block.createCuboidShape(0.0, 2.0, 2.0, 10.0, 14.0, 14.0)
	)

	val BUTTON_SHAPES = mapOf(
		BlockFace.FLOOR to Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 2.0, 14.0),
		BlockFace.CEILING to Block.createCuboidShape(2.0, 14.0, 2.0, 14.0, 16.0, 14.0),
		Direction.NORTH to Block.createCuboidShape(2.0, 2.0, 14.0, 14.0, 14.0, 16.0),
		Direction.SOUTH to Block.createCuboidShape(2.0, 2.0, 0.0, 14.0, 14.0, 2.0),
		Direction.WEST to Block.createCuboidShape(14.0, 2.0, 2.0, 16.0, 14.0, 14.0),
		Direction.EAST to Block.createCuboidShape(0.0, 2.0, 2.0, 2.0, 14.0, 14.0)
	)

	fun getLeverHitbox(face: BlockFace, facing: Direction): VoxelShape? {
		return LEVER_SHAPES[face] ?: LEVER_SHAPES[facing]
	}

	fun getButtonHitbox(face: BlockFace, facing: Direction): VoxelShape? {
		return BUTTON_SHAPES[face] ?: BUTTON_SHAPES[facing]
	}
}