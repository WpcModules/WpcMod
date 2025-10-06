package net.wapic.wpcmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.*;
import net.wapic.wpcmod.events.InventoryEvents;
import net.wapic.wpcmod.events.ParticleEvents;
import net.wapic.wpcmod.events.ScoreboardChangeEvent;
import net.wapic.wpcmod.events.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

	@Shadow
	private ClientWorld world;

	@Inject(at = @At("HEAD"), method = "onParticle")
	private void onParticle(ParticleS2CPacket packet, CallbackInfo ci) {
		if (world != null) {
			ParticleEvents.SPAWN.invoker().onSpawn(packet, world);
		}
	}

	@Inject(at = @At("HEAD"), method = "onPlaySound")
	private void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
		if (world != null) {
			SoundEvents.PLAY.invoker().onPlaySound(packet, world);
		}
	}

	@Inject(at = @At("TAIL"), method = "onScreenHandlerSlotUpdate")
	private void onSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
		InventoryEvents.SLOT_UPDATE.invoker().onSlotUpdate(packet.getSyncId(), packet.getSlot(), packet.getStack());
	}

	@Inject(at = @At("TAIL"), method = "onInventory")
	private void onSlotUpdate(InventoryS2CPacket packet, CallbackInfo ci) {
		InventoryEvents.UPDATE.invoker().onUpdate(packet.syncId(), packet.contents(), packet.cursorStack());
	}

	@Inject(at = @At("HEAD"), method = "onOpenScreen")
	private void onOpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci) {
		Screen currentScreen = MinecraftClient.getInstance().currentScreen;
		String title = packet.getName().getString();
		if (currentScreen != null && !currentScreen.getTitle().getString().equals(title)) {
			InventoryEvents.CLOSE.invoker().onClose();
		}
		InventoryEvents.OPEN.invoker().onOpen(title);
	}

	@Inject(at = @At("HEAD"), method = "onCloseScreen")
	private void onCloseScreen(CallbackInfo ci) {
		InventoryEvents.CLOSE.invoker().onClose();
	}

	@Inject(at = @At("HEAD"), method = "onTeam")
	private void onScoreboardUpdate(TeamS2CPacket packet, CallbackInfo ci) {
		if (packet.getTeamOperation() != null || packet.getPlayerListOperation() != null) return;

		TeamS2CPacket.SerializableTeam team = packet.getTeam().orElse(null);

		String prefix = team == null ? "" : team.getPrefix().getString();
		String suffix = team == null ? "" : team.getSuffix().getString();

		String line = prefix + String.join(" ", packet.getPlayerNames()) + suffix;

		ScoreboardChangeEvent.EVENT.invoker().onScoreboardChange(line);
	}
}