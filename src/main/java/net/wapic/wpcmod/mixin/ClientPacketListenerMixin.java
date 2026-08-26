package net.wapic.wpcmod.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.wapic.wpcmod.events.GuiEvents;
import net.wapic.wpcmod.events.ParticleEvents;
import net.wapic.wpcmod.events.ScoreboardChangeEvent;
import net.wapic.wpcmod.events.SoundEvents;
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
	private void onSlotUpdateAfter(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
		GuiEvents.SLOT_UPDATE.invoker().onSlotUpdate(packet.getContainerId(), packet.getSlot(), packet.getItem());
	}

	@Inject(at = @At("HEAD"), method = "handleSetPlayerTeamPacket")
	private void onScoreboardUpdate(ClientboundSetPlayerTeamPacket packet, CallbackInfo ci) {
		if (packet.getTeamAction() != null || packet.getPlayerAction() != null) return;

		ClientboundSetPlayerTeamPacket.Parameters team = packet.getParameters().orElse(null);

		String prefix = team == null ? "" : team.getPlayerPrefix().getString();
		String suffix = team == null ? "" : team.getPlayerSuffix().getString();

		String line = prefix + String.join(" ", packet.getPlayers()) + suffix;
		String unformattedLine = ChatFormatting.stripFormatting(line);

		ScoreboardChangeEvent.EVENT.invoker().onScoreboardChange(unformattedLine);
	}
}