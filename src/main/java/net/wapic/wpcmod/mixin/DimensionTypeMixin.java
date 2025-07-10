package net.wapic.wpcmod.mixin;

import net.minecraft.world.dimension.DimensionType;
import net.wapic.wpcmod.config.ConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionType.class)
public class DimensionTypeMixin {
    @Inject(method = "ambientLight", at = @At("HEAD"), cancellable = true)
    private void getDimensionAmbientLight(CallbackInfoReturnable<Float> cir){
        if(ConfigManager.INSTANCE.getConfig().getGeneralConfig().getFullbright()){
            cir.setReturnValue(1.0f);
            cir.cancel();
        }
    }
}
