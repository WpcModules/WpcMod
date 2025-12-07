package net.wapic.wpcmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {

	@ModifyReturnValue(method = "getCollisionShape", at = @At("RETURN"))
	protected VoxelShape onGetCollisionShape(VoxelShape original, @Local(argsOnly = true) BlockState state) {
		if(state.getBlock() == Blocks.CHEST || state.getBlock() == Blocks.TRAPPED_CHEST) {
			return Block.column(14.0, 0.0, 14.0);
		}

		if(state.getBlock() == Blocks.PLAYER_HEAD) {
			return Block.column(8.0, 0.0, 8.0);
		}

		return original;
	}
}
