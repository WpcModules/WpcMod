package net.wapic.wpcmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {

	@ModifyReturnValue(method = "getCollisionShape", at = @At("RETURN"))
	protected VoxelShape onGetCollisionShape(VoxelShape original, @Local(argsOnly = true) BlockState state) {
		if(state.getBlock() == Blocks.CHEST || state.getBlock() == Blocks.TRAPPED_CHEST) {
			return Block.createColumnShape(14.0, 0.0, 14.0);
		}

		if(state.getBlock() == Blocks.PLAYER_HEAD) {
			return Block.createColumnShape(8.0, 0.0, 8.0);
		}

		return original;
	}
}
