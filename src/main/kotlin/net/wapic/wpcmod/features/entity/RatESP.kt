package net.wapic.wpcmod.features.entity

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.entity.Entity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.EntityUtils.headTexture
import net.wapic.wpcmod.util.HeadTextures
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.RenderUtils.drawBoundingBox
import net.wapic.wpcmod.util.render.RenderUtils.drawTracer

class RatESP : MobGlowCache() {

	private val config get() = WpcMod.config.general.esp.rat

	init {
		WorldRenderEvents.END.register(::onRenderWorld)
	}

	private fun isRat(entity: Entity): Boolean = entity is ArmorStandEntity && entity.headTexture == HeadTextures.RAT

	private fun onRenderWorld(worldRenderContext: WorldRenderContext) {
		if(!isEnabled()) return

		for (entity in worldRenderContext.world().entities) {
			if(!isRat(entity)) continue

			val box = entity.boundingBox.withMinY(entity.boundingBox.minY + 1.4)
			if (config.box) worldRenderContext.drawBoundingBox(box, config.color.getEffectiveColour())
			if (config.tracer) worldRenderContext.drawTracer(box.center, config.color.getEffectiveColour())
		}
	}

	override fun compute(entity: Entity): Int {
		return when {
			config.glow && isRat(entity) -> config.color.getEffectiveColourRGB()
			else -> MobGlow.NO_GLOW
		}
	}

	override fun isEnabled(): Boolean {
		return Utils.getLocation() == Island.HUB
	}
}