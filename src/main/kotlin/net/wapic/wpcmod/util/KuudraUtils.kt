package net.wapic.wpcmod.util

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.mob.MagmaCubeEntity
import net.minecraft.text.Text
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.events.skyblock.KuudraEvents
import net.wapic.wpcmod.util.EntityUtils.skyBlockMaxHealth

object KuudraUtils {

	var kuudraEntity: MagmaCubeEntity? = null
	var phase: Phase? = null

	private const val START_MESSAGE: String =
		"§e[NPC] §cElle§f: Okay adventurers, I will go and fish up Kuudra!"
	private const val BUILD_PHASE_MESSAGE: String =
		"§e[NPC] §cElle§f: OMG! Great work collecting my supplies!"
	private const val STUN_PHASE_MESSAGE: String =
		"[NPC] Elle: Phew! The Ballista is finally ready! It should be strong enough to tank Kuudra's blows now!"
	private const val END_MESSAGE: String =
		"§e[NPC] §cElle§f: POW! SURELY THAT'S IT! I don't think he has any more in him!"

	fun init() {
		ClientTickEvents.END_WORLD_TICK.register(::onTick)
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)

		WorldChangeEvent.BEFORE.register {
			kuudraEntity = null
			phase = null
		}
	}

	private fun onTick(world: ClientWorld) {
		WpcMod.profiler.push("kuudra-tick")
		if (Utils.getLocation() != Island.KUUDRA) return

		if (kuudraEntity == null) {
			world.entities.find { it is MagmaCubeEntity && it.size == 30 && it.skyBlockMaxHealth == 100000.0f }?.let {
				kuudraEntity = it as MagmaCubeEntity
				WpcMod.logger.debug("set KuudraEntity to {}", it)
			}
		}
		WpcMod.profiler.pop()
	}

	private fun onMessageReceived(message: Text, actionBar: Boolean) {
		if (actionBar) return

		when (message.string) {
			START_MESSAGE -> {
				KuudraEvents.START.invoker().onStart()
				phase = Phase.SUPPLY
			}

			BUILD_PHASE_MESSAGE -> phase = Phase.BUILD
			STUN_PHASE_MESSAGE -> phase = Phase.STUN

			END_MESSAGE -> {
				KuudraEvents.END.invoker().onEnd()
				phase = Phase.KILL
			}
		}
	}

	enum class Phase {
		SUPPLY,
		BUILD,
		STUN,
		KILL;
	}
}