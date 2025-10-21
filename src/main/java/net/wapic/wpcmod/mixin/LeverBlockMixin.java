package net.wapic.wpcmod.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.wapic.wpcmod.WpcMod;
import net.wapic.wpcmod.features.dungeons.BiggerHitboxes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LeverBlock.class)
public abstract class LeverBlockMixin extends WallMountedBlock {

	protected LeverBlockMixin(Settings settings) {
		super(settings);
	}

	@Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
	public void onGetOutLineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
		if (WpcMod.config.getDungeon().getHitboxes().getLever()) {
			VoxelShape shape = BiggerHitboxes.INSTANCE.getLeverHitbox(state.get(FACE), state.get(FACING));
			if (shape != null) {
				cir.setReturnValue(shape);
			}
		}
	}
}
