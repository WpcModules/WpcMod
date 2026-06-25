package net.wapic.wpcmod.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.wapic.wpcmod.features.dungeons.BiggerHitboxes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LeverBlock.class)
public abstract class LeverBlockMixin extends FaceAttachedHorizontalDirectionalBlock {

	protected LeverBlockMixin(Properties settings) {
		super(settings);
	}

	@Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
	public void onGetOutlineShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
		VoxelShape shape = BiggerHitboxes.INSTANCE.getHitbox(state);
		if (shape != null) {
			cir.setReturnValue(shape);
		}
	}
}
