package net.wapic.wpcmod.features.entity

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.entity.Entity

object MobGlow {

	private val CACHE = hashMapOf<Entity, ChromaColour>()

	val ADDERS = mutableListOf<MobGlowCache>()

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register { _ -> clearCache() }
	}

	fun add(cache: MobGlowCache) {
		ADDERS.add(cache)
	}

	fun hasOrCompute(entity: Entity): Boolean {
		if(CACHE.containsKey(entity)) return true

		val color = compute(entity)
		if (color != null) {
			CACHE[entity] = color
			return true
		}
		return false
	}

	fun getMobGlowOrDefault(entity: Entity, default: Int): Int {
		return CACHE[entity]?.getEffectiveColourRGB() ?: default
	}

	fun clearCache() {
		CACHE.clear()
	}

	fun compute(entity: Entity): ChromaColour? {
		for (adder in ADDERS) {
			if(!adder.isEnabled()) continue

			val glowColour = adder.compute(entity)
			if (glowColour != null) return glowColour
		}
		return null
	}
}