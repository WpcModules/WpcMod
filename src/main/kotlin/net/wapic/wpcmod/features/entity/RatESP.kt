package net.wapic.wpcmod.features.entity

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.entity.decoration.ArmorStandEntity
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.EntityUtils.headTexture
import net.wapic.wpcmod.util.HeadTextures
import net.wapic.wpcmod.util.render.RenderUtils

class RatESP {

	private val config get() = WpcMod.config.general.esp.rat

	init {
		WorldRenderEvents.END.register(::onRenderWorld)
	}

	fun onRenderWorld(worldRenderContext: WorldRenderContext) {
		worldRenderContext.world().entities.forEach { entity ->
			if(entity !is ArmorStandEntity) return@forEach
			if(entity.headTexture != HeadTextures.RAT) return@forEach

			val box = entity.boundingBox.withMinY(entity.boundingBox.minY + 1.4)
			if(config.box) RenderUtils.drawBoundingBox(worldRenderContext, box, config.color.getEffectiveColour())
			if (config.tracer) RenderUtils.drawTracer(
				worldRenderContext,
				entity.x,
				entity.eyeY,
				entity.z,
				config.color.getEffectiveColour()
			)
		}
	}
}