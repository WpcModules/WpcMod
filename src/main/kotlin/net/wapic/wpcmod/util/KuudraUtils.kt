package net.wapic.wpcmod.util

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.monster.cubemob.MagmaCube
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.events.skyblock.KuudraEvents
import net.wapic.wpcmod.util.ChatUtils.removeFormatting
import net.wapic.wpcmod.util.EntityUtils.skyBlockMaxHealth

object KuudraUtils {

	var kuudraEntity: MagmaCube? = null
	var phase: Phase? = null

	private const val START_MESSAGE: String =
		"§e[NPC] §cElle§f: Okay adventurers, I will go and fish up Kuudra!"
	private const val BUILD_PHASE_MESSAGE: String =
		"§e[NPC] §cElle§f: OMG! Great work collecting my supplies!"
	private const val STUN_PHASE_MESSAGE: String =
		"[NPC] Elle: Phew! The Ballista is finally ready! It should be strong enough to tank Kuudra's blows now!"
	private const val KILL_PHASE_MESSAGE: String =
		"§e[NPC] §cElle§f: POW! SURELY THAT'S IT! I don't think he has any more in him!"
	private const val KUUDRA_END_MESSAGE: String = "KUUDRA DOWN!"

	fun init() {
		ClientTickEvents.END_LEVEL_TICK.register(::onTick)
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)

		WorldChangeEvent.BEFORE.register {
			kuudraEntity = null
			phase = null
		}
	}

	private fun onTick(level: ClientLevel) {
		if (Utils.getLocation() != Island.KUUDRA) return

		if (kuudraEntity == null) {
			level.entitiesForRendering().find { it is MagmaCube && it.size == 30 && it.skyBlockMaxHealth == 100000.0f }
				?.let {
				kuudraEntity = it as MagmaCube
				WpcMod.LOGGER.debug("set KuudraEntity to {}", it)
			}
		}
	}

	private fun onMessageReceived(message: Component, actionBar: Boolean) {
		if (actionBar) return

		when (message.string) {
			START_MESSAGE -> {
				KuudraEvents.START.invoker().onStart()
				phase = Phase.SUPPLY
			}

			BUILD_PHASE_MESSAGE -> phase = Phase.BUILD
			STUN_PHASE_MESSAGE -> phase = Phase.STUN
			KILL_PHASE_MESSAGE -> phase = Phase.KILL


		}

		if (message.string.removeFormatting().trim() == KUUDRA_END_MESSAGE) {
			WpcMod.LOGGER.debug("Kuudra Ended")
			KuudraEvents.END.invoker().onEnd()
			phase = null
		}
	}

	enum class Phase {
		SUPPLY,
		BUILD,
		STUN,
		KILL;
	}
}