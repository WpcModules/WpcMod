package net.wapic.wpcmod.mixin;


import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.wapic.wpcmod.features.dungeons.BiggerHitboxes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin {

	@Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
	public void onGetOutlineShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
		VoxelShape shape = BiggerHitboxes.INSTANCE.getHitbox(state);
		if (shape != null) {
			cir.setReturnValue(shape);
		}
	}
}
