package net.wapic.wpcmod.features.garden

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.features.entity.MobGlowCache
import net.wapic.wpcmod.util.EntityUtils.headTexture
import net.wapic.wpcmod.util.HeadTextures
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.WorldRenderContext

object PestESP : MobGlowCache() {
	val config get() = WpcMod.config.garden.esp.pest

	fun init() {
		WorldRenderEvent.EVENT.register(::onRenderWorld)
	}

	fun onRenderWorld(worldRenderContext: WorldRenderContext) {
		if (Utils.getLocation() != Island.GARDEN) return
		if (!config.tracer && !config.box) return
		val deltaTicks = worldRenderContext.tickCounter.getGameTimeDeltaPartialTick(true)

		val entities = worldRenderContext.level.entitiesForRendering().filter(::isPest)

		for (entity in entities) {
			val pos = entity.getEyePosition(deltaTicks)
			if (config.box) worldRenderContext.drawBoundingBox(pos, 0.8f, 0.8f, config.color)
			if (config.tracer) worldRenderContext.drawTracer(pos, config.color)
		}
	}

	fun isPest(entity: Entity): Boolean {
		return entity is ArmorStand && entity.headTexture in HeadTextures.allPests
	}

	override fun compute(entity: Entity): ChromaColour? {
		return if (isPest(entity)) config.color else null
	}

	override fun isEnabled(): Boolean {
		return config.glow && Utils.getLocation() == Island.GARDEN
	}
}