package net.wapic.wpcmod.features.entity

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey
import net.minecraft.client.Minecraft
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.entity.Entity
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.util.render.WpcModExtractionContext
import net.wapic.wpcmod.util.render.state.EntityState

object EspCache {

	private val CACHE = hashMapOf<Entity, EntityState>()

	@JvmField
	val ENTITY_HAS_CUSTOM_GLOW: RenderStateDataKey<Boolean> =
		RenderStateDataKey.create { "WpcMod entity has custom glow" }
	private val ADDERS = mutableListOf<EspFeature>()

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register(::clearCache)
		WorldRenderEvent.EVENT.register(::onRenderWorld)
	}

	private fun clearCache(client: Minecraft) = CACHE.clear()
	private fun onRenderWorld(context: WpcModExtractionContext, profiler: ProfilerFiller) {
		profiler.push("cache")
		for (entity in context.level.entitiesForRendering()) {
			val state = getOrCompute(entity) ?: continue
			context.entityESP(entity, state)
		}
		profiler.pop()
	}

	fun add(feature: EspFeature) {
		ADDERS.add(feature)
	}

	fun getOrCompute(entity: Entity): EntityState? {
		if (CACHE.containsKey(entity)) return CACHE[entity]

		val renderState = compute(entity)
		if (renderState != null) CACHE[entity] = renderState
		return renderState
	}

	fun compute(entity: Entity): EntityState? {
		for (adder in ADDERS) {
			if (!adder.isEnabled()) continue

			val renderState = adder.compute(entity)
			if (renderState != null) return renderState
		}
		return null
	}
}