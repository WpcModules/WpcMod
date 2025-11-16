package net.wapic.wpcmod.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.wapic.wpcmod.features.dungeons.BiggerHitboxes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin {

	@Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
	public void onGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
		VoxelShape shape = BiggerHitboxes.INSTANCE.getHitbox(state);
		if (shape != null) {
			cir.setReturnValue(shape);
		}
	}
}
