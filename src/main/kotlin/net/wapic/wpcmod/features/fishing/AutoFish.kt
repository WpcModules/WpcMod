package net.wapic.wpcmod.features.fishing

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.projectile.FishingHook
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.item.Items
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.EntityUtils.getArmorStandsByEntity
import net.wapic.wpcmod.util.MC
import kotlin.random.Random

object AutoFish {

	private val config get() = WpcMod.config.fishing.autofish
	@Volatile
	private var preventFutureRodUse: Boolean = false
	private val slugDelayInTicks: Int get() = if (config.slugPet) 200 else 400

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
	}

	private fun onTick(client: Minecraft) {
		if(!config.enabled || preventFutureRodUse) return

		val fishHook = client.player?.fishing ?: return

		if (config.slugFish && fishHook.tickCount < slugDelayInTicks) return

		if (fishHook.hasCaughtFish) {
			preventFutureRodUse = true
			useRod(client)
		}
	}

	private fun useRod(client: Minecraft) = WpcMod.coroutineScope.launch {
		try {
			val castCount = if (config.disableRecast) 1 else 2

			val minDelay = config.minDelay.toLong()
			val maxDelay = minDelay + 100L

			repeat(castCount) { i ->
				val castDelay = Random.nextLong(minDelay, maxDelay)
				delay(castDelay)

				val isHoldingRod = client.player?.isHolding(Items.FISHING_ROD) == true
				val inSkyBlockMenu = client.player?.containerMenu !is InventoryMenu
				val isSafe = !(config.safeMode && inSkyBlockMenu)

				if (isHoldingRod && isSafe) MC.useItem()
			}

			delay(350) // Delay to prevent false positive from old armor stand
		} finally {
			preventFutureRodUse = false
		}
	}

	private val FishingHook.hasCaughtFish: Boolean get() = getArmorStandsByEntity(this).any { it.name.string == "!!!" }
}