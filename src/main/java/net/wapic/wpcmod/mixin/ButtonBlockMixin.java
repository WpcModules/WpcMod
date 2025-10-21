package net.wapic.wpcmod.mixin;


import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
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

@Mixin(ButtonBlock.class)
public abstract class ButtonBlockMixin extends WallMountedBlock {

	protected ButtonBlockMixin(Settings settings) {
		super(settings);
	}

	@Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
	public void onGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
		if (WpcMod.config.getDungeon().getHitboxes().getButton()) {
			VoxelShape shape = BiggerHitboxes.INSTANCE.getButtonHitbox(state.get(FACE), state.get(FACING));
			if (shape != null) {
				cir.setReturnValue(shape);
			}
		}
	}
}
