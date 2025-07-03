package net.wapic.wpcmod.mixin;

import net.minecraft.world.dimension.DimensionType;
import net.wapic.wpcmod.WpcMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionType.class)
public class DimensionTypeMixin {
    @Inject(method = "ambientLight", at = @At("HEAD"), cancellable = true)
    private void getDimensionAmbientLight(CallbackInfoReturnable<Float> cir){
        if(WpcMod.config.getInstance().generalConfig.fullbright){
            cir.setReturnValue(1.0f);
            cir.cancel();
        }
    }
}
