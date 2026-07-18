package net.wapic.wpcmod.features.fishing

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.FishingHook
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.util.getNearbyArmorStands
import net.wapic.wpcmod.util.MC
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

object AutoFish {

	private val config get() = WpcMod.config.fishing.autofish
	private var isProcessing = AtomicBoolean(false)
	private val slugDelayInTicks: Int get() = if (config.slugPet) 200 else 400
	private var lastRodCast = 0
	private var cachedHook: WeakReference<FishingHook>? = null

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
		UseItemCallback.EVENT.register(::onUseItem)
		WorldChangeEvent.BEFORE.register(::reset)
	}

	private fun reset(level: ClientLevel) {
		cachedHook = null
		lastRodCast = 0
	}

	private fun onUseItem(player: Player, level: Level, hand: InteractionHand): InteractionResult {
		if (player.isHolding(Items.FISHING_ROD)) {
			lastRodCast = player.tickCount
		}
		return InteractionResult.PASS
	}

	private fun onTick(client: Minecraft) {
		if (!config.enabled || isProcessing.get() || lastRodCast == 0) return
		val player = client.player ?: return
		val level = client.level ?: return
		if (player.tickCount - lastRodCast < 10) return

		val fishHook = getActiveHook(player, level)
		if (fishHook == null) {
			lastRodCast = 0
			return
		}

		if (config.slugFish && fishHook.tickCount < slugDelayInTicks) return

		if (fishHook.hasCaughtFish) {
			useRod(client)
		}
	}

	private fun useRod(client: Minecraft) = WpcMod.coroutineScope.launch {
		if (!isProcessing.compareAndSet(false, true)) return@launch

		try {
			val castCount = if (config.disableRecast) 1 else 2

			val minDelay = config.minDelay.toLong()
			val maxDelay = minDelay + 100L

			repeat(castCount) { i ->
				val castDelay = Random.nextLong(minDelay, maxDelay).milliseconds
				delay(castDelay)

				val isHoldingRod = client.player?.isHolding(Items.FISHING_ROD) == true
				val inSkyBlockMenu = client.player?.containerMenu !is InventoryMenu
				val isSafe = !(config.safeMode && inSkyBlockMenu)

				if (isHoldingRod && isSafe) {
					MC.useItem()
				}
			}

			delay(350.milliseconds) // Delay to prevent false positive from old armor stand
		} finally {
			isProcessing.set(false)
			cachedHook = null
		}
	}

	private fun getActiveHook(player: LocalPlayer, level: ClientLevel): FishingHook? {
		cachedHook?.get()?.takeIf { it.isAlive }?.let {
			return it
		}

		val hook = player.fishing ?: level.entitiesForRendering()
			.filterIsInstance<FishingHook>()
			.firstOrNull { it.owner == player }
		if (hook != null) cachedHook = WeakReference(hook)

		return hook
	}

	private val FishingHook.hasCaughtFish: Boolean get() = this.getNearbyArmorStands().any { it.name.string == "!!!" }
}