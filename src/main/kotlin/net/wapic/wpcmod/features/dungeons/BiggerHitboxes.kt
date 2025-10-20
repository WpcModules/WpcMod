package net.wapic.wpcmod.features.dungeons

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ButtonBlock
import net.minecraft.block.LeverBlock
import net.minecraft.block.enums.BlockFace
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape

object BiggerHitboxes {

	val LEVER_FLOOR: VoxelShape = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 8.0, 14.0)
	val LEVER_CEILING: VoxelShape = Block.createCuboidShape(2.0, 8.0, 2.0, 14.0, 16.0, 14.0)
	val LEVER_WALL_NORTH: VoxelShape = Block.createCuboidShape(2.0, 2.0, 8.0, 14.0, 14.0, 16.0)
	val LEVER_WALL_SOUTH: VoxelShape = Block.createCuboidShape(2.0, 2.0, 0.0, 14.0, 14.0, 8.0)
	val LEVER_WALL_WEST: VoxelShape = Block.createCuboidShape(8.0, 2.0, 2.0, 16.0, 14.0, 14.0)
	val LEVER_WALL_EAST: VoxelShape = Block.createCuboidShape(0.0, 2.0, 2.0, 8.0, 14.0, 14.0)

	val BUTTON_FLOOR: VoxelShape = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 2.0, 14.0)
	val BUTTON_CEILING: VoxelShape = Block.createCuboidShape(2.0, 14.0, 2.0, 14.0, 16.0, 14.0)
	val BUTTON_WALL_NORTH: VoxelShape = Block.createCuboidShape(2.0, 2.0, 14.0, 14.0, 14.0, 16.0)
	val BUTTON_WALL_SOUTH: VoxelShape = Block.createCuboidShape(2.0, 2.0, 0.0, 14.0, 14.0, 2.0)
	val BUTTON_WALL_WEST: VoxelShape = Block.createCuboidShape(14.0, 2.0, 2.0, 16.0, 14.0, 14.0)
	val BUTTON_WALL_EAST: VoxelShape = Block.createCuboidShape(0.0, 2.0, 2.0, 2.0, 14.0, 14.0)

	fun getLeverHitbox(blockState: BlockState): VoxelShape {
		val face = blockState.get(LeverBlock.FACE)
		val facing = blockState.get(LeverBlock.FACING)

		if (face == BlockFace.FLOOR)
			return LEVER_FLOOR
		if (face == BlockFace.CEILING)
			return LEVER_CEILING

		val shape = when (facing) {
			Direction.NORTH -> LEVER_WALL_NORTH
			Direction.SOUTH -> LEVER_WALL_SOUTH
			Direction.WEST -> LEVER_WALL_WEST
			Direction.EAST -> LEVER_WALL_EAST
			else -> blockState.cullingShape
		}

		return shape
	}

	fun getButtonHitbox(blockState: BlockState): VoxelShape {
		val face = blockState.get(ButtonBlock.FACE)
		val facing = blockState.get(ButtonBlock.FACING)

		if (face == BlockFace.FLOOR)
			return BUTTON_FLOOR
		if (face == BlockFace.CEILING)
			return BUTTON_CEILING

		val shape = when (facing) {
			Direction.NORTH -> BUTTON_WALL_NORTH
			Direction.SOUTH -> BUTTON_WALL_SOUTH
			Direction.WEST -> BUTTON_WALL_WEST
			Direction.EAST -> BUTTON_WALL_EAST
			else -> blockState.cullingShape
		}

		return shape
	}

}