package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.Holder
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.phys.Vec3
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object SoundEvents {

	@JvmField
	val PLAY: Event<PlaySound> = EventFactory.createArrayBacked(PlaySound::class.java) { listeners ->
		PlaySound { sound, source, pos, vol, pitch, seed, level, callbackInfo ->
			for (listener in listeners) {
				listener.onPlaySound(sound, source, pos, vol, pitch, seed, level, callbackInfo)
			}
		}
	}

	fun interface PlaySound {

		fun onPlaySound(
			sound: Holder<SoundEvent>,
			source: SoundSource,
			pos: Vec3,
			vol: Float,
			pitch: Float,
			seed: Long,
			level: ClientLevel,
			callbackInfo: CallbackInfo
		)
	}
}
