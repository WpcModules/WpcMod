package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.network.protocol.game.ClientboundSoundPacket

object SoundEvents {

	@JvmField
	val PLAY: Event<PlaySound> = EventFactory.createArrayBacked(PlaySound::class.java) { listeners ->
		PlaySound { packet, world ->
			for (listener in listeners) {
				listener.onPlaySound(packet, world)
			}
		}
	}

	fun interface PlaySound {
		fun onPlaySound(packet: ClientboundSoundPacket, world: ClientLevel)
	}
}
