package net.wapic.wpcmod.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.wapic.wpcmod.events.GuiEvents;
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

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin extends AbstractClientPlayer {

	@Shadow
	public ClientInput input;

	@Shadow
	@Final
	protected Minecraft minecraft;

	@Unique
	private final KeyboardInput dummy = new DummyInput(null);
	@Unique
	private ClientInput realInput;

	public LocalPlayerMixin(ClientLevel world, GameProfile profile) {
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

	@Inject(method = "isControlledCamera", at = @At("HEAD"), cancellable = true)
	private void freecam_allowPlayerMovement(CallbackInfoReturnable<Boolean> cir) {
		if (Freecam.Companion.isEnabled() && Freecam.Companion.getOriginalCameraWasPlayer()) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "swing", at = @At("HEAD"), cancellable = true)
	private void freecam_disableSwing(CallbackInfo ci) {
		if (Freecam.Companion.isEnabled()) {
			ci.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "clientSideCloseContainer")
	public void onCloseScreen(CallbackInfo ci) {
		GuiEvents.CLOSE.invoker().onClose();
	}
}
