package net.wapic.wpcmod.util

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.mob.MagmaCubeEntity
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.skyblock.KuudraEvents
import net.wapic.wpcmod.util.EntityUtils.skyBlockMaxHealth

object KuudraUtils {

	var kuudraEntity: MagmaCubeEntity? = null
	var phase: Phase? = null

	fun init() {
		ClientTickEvents.END_WORLD_TICK.register(::onTick)

		ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register { _, _ ->
			kuudraEntity = null
			phase = null
		}

		KuudraEvents.START.register {
			phase = Phase.SUPPLY
		}
	}

	private fun onTick(world: ClientWorld) {
		if (Utils.getLocation() != Island.KUUDRA) return

		if (kuudraEntity == null) {
			world.entities.find { it is MagmaCubeEntity && it.size == 30 && it.skyBlockMaxHealth == 100000.0f }?.let {
				kuudraEntity = it as MagmaCubeEntity
				WpcMod.logger.debug("set KuudraEntity to {}", it);
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