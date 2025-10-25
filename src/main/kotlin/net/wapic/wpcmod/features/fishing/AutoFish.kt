package net.wapic.wpcmod.features.fishing

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.projectile.FishingBobberEntity
import net.minecraft.item.Items
import net.minecraft.screen.PlayerScreenHandler
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.mixin.accessors.MinecraftClientAccessor
import net.wapic.wpcmod.util.EntityUtils.getArmorStandsByEntity
import net.wapic.wpcmod.util.MC
import kotlin.random.Random

object AutoFish {

	private val config get() = WpcMod.config.fishing.autofish
	private var preventFutureRodUse: Boolean = false
	private val slugDelayInTicks: Int get() = 20 * if (config.slugPet) 10 else 20

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
	}

	private fun onTick(client: MinecraftClient) {
		if(!config.enabled || preventFutureRodUse) return

		val fishHook = client.player?.fishHook ?: return

		if (config.slugFish && fishHook.age < slugDelayInTicks) return

		if (fishHook.hasCaughtFish) {
			preventFutureRodUse = true
			useRod(client)
		}
	}

	private fun useRod(client: MinecraftClient) = WpcMod.coroutineScope.launch {
		val cast = if(config.recast) 2 else 1

		val minDelay = config.minDelay.toLong()
		val maxDelay = minDelay + 100L

		repeat(cast) {
			val castDelay = Random.nextLong(minDelay, maxDelay)
			delay(castDelay)

			val isHoldingRod = client.player?.isHolding(Items.FISHING_ROD) == true
			val notInGui = client.player?.currentScreenHandler is PlayerScreenHandler

			if (isHoldingRod && notInGui) {
				MC.runOnThread {
					(client as MinecraftClientAccessor).doItemUse_WpcMod()
				}
			}
		}

		delay(200) // Delay to prevent false positive from old armor stand
		preventFutureRodUse = false
	}

	private val FishingBobberEntity.hasCaughtFish: Boolean get() = getArmorStandsByEntity(this).any { it.name.string == "!!!" } && isInFluid
}