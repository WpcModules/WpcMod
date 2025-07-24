package net.wapic.wpcmod.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.wapic.wpcmod.events.InventoryEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

	@Inject(at = @At("HEAD"), method = "closeScreen")
	public void onCloseScreen(CallbackInfo ci) {
		InventoryEvents.CLOSE.invoker().onClose();
	}
}
