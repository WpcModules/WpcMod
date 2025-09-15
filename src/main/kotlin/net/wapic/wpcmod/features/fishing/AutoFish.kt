package net.wapic.wpcmod.features.fishing

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.projectile.FishingBobberEntity
import net.minecraft.item.Items
import net.minecraft.util.Hand
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.EntityUtils
import kotlin.random.Random

class AutoFish {

	private val config get() = WpcMod.config.fishing.autofish
	private var preventFutureRodUse: Boolean = false

	init {
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
	}

	private fun onTick(client: MinecraftClient) {
		if(!config.enabled || preventFutureRodUse) return
		val fishHook = client.player?.fishHook ?: return

		if(isHookReady(fishHook)) {
			useRod(client)
		}
	}

	private fun useRod(client: MinecraftClient) = WpcMod.coroutineScope.launch {
		preventFutureRodUse = true

		val cast = if(config.recast) 2 else 1

		val minDelay = config.minDelay.toLong()
		val maxDelay = minDelay + 150L

		val castDelay = Random.nextLong(minDelay, maxDelay)

		repeat(cast) {
			delay(castDelay)

			val isHoldingRod = client.player?.isHolding(Items.FISHING_ROD) == true
			if(isHoldingRod) {
				client.interactionManager?.interactItem(client.player, Hand.MAIN_HAND)
			}
		}

		preventFutureRodUse = false
	}

	private fun isHookReady(hook: FishingBobberEntity): Boolean {
		val armorStands = EntityUtils.getArmorStandsByEntity(hook)
		return armorStands.any { it.name.string == "!!!" }
	}
}