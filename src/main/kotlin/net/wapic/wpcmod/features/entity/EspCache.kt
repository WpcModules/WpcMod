package net.wapic.wpcmod.features.entity

import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents
import net.minecraft.util.profiling.Profiler
import net.minecraft.world.entity.Entity
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.util.render.WorldRenderContext
import net.wapic.wpcmod.util.render.state.EspRenderState

object EspCache {

	private val CACHE = hashMapOf<Entity, EspRenderState>()

	@JvmField
	val ENTITY_HAS_CUSTOM_GLOW: RenderStateDataKey<Boolean> =
		RenderStateDataKey.create { "WpcMod entity has custom glow" }
	private val ADDERS = mutableListOf<EspFeature>()

	fun init() {
		LevelExtractionEvents.END_EXTRACTION.register(::rebuildCache)
		WorldRenderEvent.EVENT.register(::renderEsp)
	}

	private fun rebuildCache(levelExtractionContext: LevelExtractionContext) {
		val profiler = Profiler.get()
		profiler.push("WpcModRebuildCache")
		val level = levelExtractionContext.level()
		CACHE.clear()
		level.entitiesForRendering().forEach(::getOrCompute)
		profiler.pop()
	}

	private fun renderEsp(worldRenderContext: WorldRenderContext) {
		worldRenderContext.profiler.push("Render-ESPCache")
		for ((config, width, height, pos) in CACHE.values) {
			if (config.box) worldRenderContext.drawBoundingBox(pos, width, height, config.color)
			if (config.tracer) worldRenderContext.drawTracer(pos, config.color, config.tracerWidth)
		}
		worldRenderContext.profiler.pop()
	}

	fun add(feature: EspFeature) {
		ADDERS.add(feature)
	}

	fun getOrCompute(entity: Entity): EspRenderState? {
		if (CACHE.containsKey(entity)) return CACHE[entity]

		val renderState = compute(entity)
		if (renderState != null) CACHE[entity] = renderState
		return renderState
	}

	fun compute(entity: Entity): EspRenderState? {
		for (adder in ADDERS) {
			if (!adder.isEnabled()) continue

			val renderState = adder.compute(entity)
			if (renderState != null) return renderState
		}
		return null
	}
}