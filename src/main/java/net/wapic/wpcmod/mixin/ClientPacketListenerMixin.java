package net.wapic.wpcmod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.protocol.game.*;
import net.wapic.wpcmod.events.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

	@Shadow
	private ClientLevel level;

	@Inject(at = @At("HEAD"), method = "handleParticleEvent")
	private void onParticle(ClientboundLevelParticlesPacket packet, CallbackInfo ci) {
		if (level != null) {
			ParticleEvents.SPAWN.invoker().onSpawn(packet, level);
		}
	}

	@Inject(at = @At("HEAD"), method = "handleSoundEvent")
	private void onPlaySound(ClientboundSoundPacket packet, CallbackInfo ci) {
		if (level != null) {
			SoundEvents.PLAY.invoker().onPlaySound(packet, level);
		}
	}

	@Inject(at = @At("TAIL"), method = "handleContainerSetSlot")
	private void onSlotUpdate(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
		GuiEvents.SLOT_UPDATE.invoker().onSlotUpdate(packet.getContainerId(), packet.getSlot(), packet.getItem());
	}

	@Inject(at = @At("HEAD"), method = "handleOpenScreen")
	private void onOpenScreen(ClientboundOpenScreenPacket packet, CallbackInfo ci) {
		Screen currentScreen = Minecraft.getInstance().screen;
		String title = packet.getTitle().getString();
		if (currentScreen != null && !currentScreen.getTitle().getString().equals(title)) {
			GuiEvents.CLOSE.invoker().onClose();
		}
		GuiEvents.OPEN.invoker().onOpen(title);
	}

	@Inject(at = @At("HEAD"), method = "handleContainerClose")
	private void onCloseScreen(CallbackInfo ci) {
		GuiEvents.CLOSE.invoker().onClose();
	}

	@Inject(at = @At("HEAD"), method = "handleSetPlayerTeamPacket")
	private void onScoreboardUpdate(ClientboundSetPlayerTeamPacket packet, CallbackInfo ci) {
		if (packet.getTeamAction() != null || packet.getPlayerAction() != null) return;

		ClientboundSetPlayerTeamPacket.Parameters team = packet.getParameters().orElse(null);

		String prefix = team == null ? "" : team.getPlayerPrefix().getString();
		String suffix = team == null ? "" : team.getPlayerSuffix().getString();

		String line = prefix + String.join(" ", packet.getPlayers()) + suffix;

		ScoreboardChangeEvent.EVENT.invoker().onScoreboardChange(line);
	}
}