package net.wapic.wpcmod.features.entity

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.util.EntityUtils.headTexture
import net.wapic.wpcmod.util.HeadTextures
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.WorldRenderContext

object RatESP : MobGlowCache() {

	private val config get() = WpcMod.config.general.esp.rat

	fun init() {
		WorldRenderEvent.EVENT.register(::onRenderWorld)
	}

	private fun isRat(entity: Entity): Boolean = entity is ArmorStand && entity.headTexture == HeadTextures.RAT

	private fun onRenderWorld(worldRenderContext: WorldRenderContext) {
		if(!isEnabled()) return
		worldRenderContext.profiler.push("rat-esp")
		for (entity in worldRenderContext.world.entitiesForRendering()) {
			if(!isRat(entity)) continue

			val box = entity.boundingBox.setMinY(entity.boundingBox.minY + 1.4)
			if (config.box) worldRenderContext.drawBoundingBox(box, config.color)
			if (config.tracer) worldRenderContext.drawTracer(box.center, config.color)
		}
		worldRenderContext.profiler.pop()
	}

	override fun compute(entity: Entity): ChromaColour? {
		return when {
			config.glow && isRat(entity) -> config.color
			else -> null
		}
	}

	override fun isEnabled(): Boolean {
		return Utils.getLocation() == Island.HUB
	}
}