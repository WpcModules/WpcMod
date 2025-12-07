package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket

object ParticleEvents {

	@JvmField
	val SPAWN: Event<ParticleSpawn> = EventFactory.createArrayBacked(ParticleSpawn::class.java) { listeners ->
		ParticleSpawn { packet, world ->
			for (listener in listeners) {
				listener.onSpawn(packet, world)
			}
		}
	}

	fun interface ParticleSpawn {
		fun onSpawn(packet: ClientboundLevelParticlesPacket, world: ClientLevel)
	}
}
