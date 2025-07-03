package net.wapic.wpcmod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;

public interface PacketEvents {
    Event<ParticleEvent> PARTICLE = EventFactory.createArrayBacked(ParticleEvent.class, (listeners) -> (packet, world) -> {
            for (ParticleEvent listener : listeners) {
                listener.onParticle(packet, world);
            }
    });

    Event<SoundEvent> PLAY_SOUND = EventFactory.createArrayBacked(SoundEvent.class, (listeners) -> (packet, world) -> {
        for (SoundEvent listener : listeners) {
            listener.onPlaySound(packet, world);
        }
    });

    interface ParticleEvent {
        void onParticle(ParticleS2CPacket packet, ClientWorld world);
    }

    interface SoundEvent {
        void onPlaySound(PlaySoundS2CPacket packet, ClientWorld world);
    }
}
