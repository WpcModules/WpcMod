package net.wapic.wpcmod.features.entity

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents
import net.minecraft.client.Minecraft
import net.minecraft.util.profiling.Profiler
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand
import net.wapic.wpcmod.config.components.GlowableESPConfig
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.util.EntityUtils.getRenderPos
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.render.WorldRenderContext

object EspCache {

	private val CACHE = hashMapOf<Entity, GlowableESPConfig>()

	@JvmField
	val ENTITY_HAS_CUSTOM_GLOW: RenderStateDataKey<Boolean> =
		RenderStateDataKey.create { "WpcMod entity has custom glow" }
	private val ADDERS = mutableListOf<EspFeature>()

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register(::rebuildCache)
		WorldRenderEvent.EVENT.register(::renderEsp)
	}

	private fun rebuildCache(client: Minecraft) {
		val profiler = Profiler.get()
		val level = client.level ?: return
		profiler.push("build-esp-cache")
		CACHE.clear()
		level.entitiesForRendering().forEach(::getOrCompute)
		profiler.pop()
	}

	private fun renderEsp(worldRenderContext: WorldRenderContext) {
		worldRenderContext.profiler.push("render-esp")

		for ((entity, config) in CACHE) {
			val width = if (entity is ArmorStand || entity is Display.ItemDisplay) 0.8f else entity.bbWidth
			val height = if (entity is ArmorStand || entity is Display.ItemDisplay) 0.8f else entity.bbHeight
			if (config.box) worldRenderContext.drawBoundingBox(entity.eyePosition, width, height, config.color)
			if (config.tracer) worldRenderContext.drawTracer(entity.eyePosition, config.color, config.tracerWidth)
		}

		worldRenderContext.profiler.pop()
	}

	fun add(feature: EspFeature) {
		ADDERS.add(feature)
	}

	fun getOrCompute(entity: Entity): GlowableESPConfig? {
		if (CACHE.containsKey(entity)) return CACHE[entity]

		val config = compute(entity)
		if (config != null) CACHE[entity] = config
		return config
	}

	fun compute(entity: Entity): GlowableESPConfig? {
		for (adder in ADDERS) {
			if (!adder.isEnabled()) continue

			val config = adder.compute(entity)
			if (config != null) return config
		}
		return null
	}
}