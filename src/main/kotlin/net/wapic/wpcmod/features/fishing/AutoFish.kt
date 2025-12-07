package net.wapic.wpcmod.features.fishing

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.projectile.FishingHook
import net.minecraft.world.item.Items
import net.minecraft.world.inventory.InventoryMenu
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.mixin.accessors.MinecraftAccessor
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
		val cast = if(config.recast) 2 else 1

		val minDelay = config.minDelay.toLong()
		val maxDelay = minDelay + 100L

		repeat(cast) {
			val castDelay = Random.nextLong(minDelay, maxDelay)
			delay(castDelay)

			val isHoldingRod = client.player?.isHolding(Items.FISHING_ROD) == true
			val notInGui = client.player?.containerMenu is InventoryMenu

			if (isHoldingRod && notInGui) {
				MC.runOnThread {
					(client as MinecraftAccessor).doItemUse_WpcMod()
				}
			}
		}

		delay(200) // Delay to prevent false positive from old armor stand
		preventFutureRodUse = false
	}

	private val FishingHook.hasCaughtFish: Boolean get() = getArmorStandsByEntity(this).any { it.name.string == "!!!" } && isInLiquid
}