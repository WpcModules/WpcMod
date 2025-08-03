package net.wapic.wpcmod.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.wapic.wpcmod.events.InventoryEvents;
import net.wapic.wpcmod.features.general.Freecam;
import net.wapic.wpcmod.util.freecam.DummyInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

	@Shadow
	public Input input;

	@Shadow
	@Final
	protected MinecraftClient client;

	@Unique
	private final KeyboardInput dummy = new DummyInput(null);
	@Unique
	private Input realInput;

	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void freecam_disableMovementInputsPre(CallbackInfo ci) {
		if (Freecam.Companion.isEnabled()) {
			this.realInput = this.input;
			this.input = this.dummy;
		}
	}

	@Inject(method = "tick", at = @At("RETURN"))
	private void freecam_disableMovementInputsPost(CallbackInfo ci) {
		if (this.realInput != null) {
			this.input = this.realInput;
			this.realInput = null;
		}
	}

	@Inject(method = "isCamera", at = @At("HEAD"), cancellable = true)
	private void freecam_allowPlayerMovement(CallbackInfoReturnable<Boolean> cir) {
		if (Freecam.Companion.isEnabled() && Freecam.Companion.getOriginalCameraWasPlayer()) {
			cir.setReturnValue(true);
		}
	}


	@Inject(at = @At("HEAD"), method = "closeScreen")
	public void onCloseScreen(CallbackInfo ci) {
		InventoryEvents.CLOSE.invoker().onClose();
	}
}
