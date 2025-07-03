package net.wapic.wpcmod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;

public interface PacketEvents {
    Event<ParticleEvent> PARTICLE = EventFactory.createArrayBacked(ParticleEvent.class, (listeners) -> (packet) -> {
            for (ParticleEvent listener : listeners) {
                listener.onParticle(packet);
            }
    });

    interface ParticleEvent {
        void onParticle(ParticleS2CPacket packet);
    }
}
