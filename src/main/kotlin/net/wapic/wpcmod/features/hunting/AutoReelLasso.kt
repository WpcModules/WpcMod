package net.wapic.wpcmod.features.hunting

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.ambient.Bat
import net.minecraft.world.entity.decoration.ArmorStand
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.EntityEvents
import net.wapic.wpcmod.util.ItemUtils.skyblockId
import net.wapic.wpcmod.util.MC
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

object AutoReelLasso {
	private val config get() = WpcMod.config.hunting

	private val LASSO_IDS = listOf("ABYSMAL_LASSO", "VINERIP_LASSO", "ENTANGLER_LASSO", "EVERSTRETCH_LASSO")
	private var isProcessing = false

	// Skyblock Lasso is a leash attached to a Bat
	private var potentialLeash: Bat? = null

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
		EntityEvents.SPAWN.register(::onEntityAdded)
	}

	fun onEntityAdded(entity: Entity) {
		if (!config.autoReel) return
		if (entity is Bat && MC.player?.mainHandItem?.skyblockId in LASSO_IDS) {
			potentialLeash = entity
		}
	}

	fun clearLeash() {
		potentialLeash = null
	}

	fun onTick(client: Minecraft) {
		if (potentialLeash == null || isProcessing) return
		if (potentialLeash?.isAlive == false) return clearLeash()

		val leashEntity = potentialLeash?.takeIf { it.leashHolder == client.player } ?: return clearLeash()

		if(leashEntity.canBeReeled()){
			isProcessing = true
			reelLasso()
		}
	}

	fun reelLasso() = WpcMod.coroutineScope.launch {
		delay(Random.nextLong(50, 150).milliseconds)
		MC.useItem()
		delay(400.milliseconds) // Prevent redetecting old leash
		isProcessing = false
	}

	private fun Entity.canBeReeled(): Boolean {
		val entities = this.level().getEntitiesOfClass(ArmorStand::class.java, this.boundingBox.expandTowards(1.0, 2.0, 1.0))
		return entities.any { it.name.string == "REEL" }
	}
}