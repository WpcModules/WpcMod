package net.wapic.wpcmod.features.entity

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.entity.Entity

object MobGlow {

	const val NO_GLOW = 0

	private val CACHE = hashMapOf<Entity, Int>()
	val ADDERS = mutableListOf<MobGlowCache>()

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register { _ -> clearCache() }
	}

	fun add(cache: MobGlowCache) {
		ADDERS.add(cache)
	}

	fun CacheIsNotEmpty(): Boolean {
		return CACHE.isNotEmpty()
	}

	fun hasOrCompute(entity: Entity): Boolean {
		if(CACHE.containsKey(entity)) return true

		val color = compute(entity)
		if(color != NO_GLOW) {
			CACHE[entity] = color
			return true
		}
		return false
	}

	fun getMobGlow(entity: Entity): Int? {
		return CACHE[entity]
	}

	fun getMobGlowOrDefault(entity: Entity, default: Int): Int {
		return CACHE.getOrDefault(entity, default)
	}

	fun clearCache() {
		CACHE.clear()
	}

	fun compute(entity: Entity): Int {
		for (adder in ADDERS) {
			if(!adder.isEnabled()) continue

			val glowColour = adder.compute(entity)
			if(glowColour != NO_GLOW) return glowColour
		}
		return NO_GLOW
	}
}