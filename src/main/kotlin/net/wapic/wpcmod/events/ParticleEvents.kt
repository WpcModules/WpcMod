package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.world.ClientWorld
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket

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
		fun onSpawn(packet: ParticleS2CPacket, world: ClientWorld)
	}
}
