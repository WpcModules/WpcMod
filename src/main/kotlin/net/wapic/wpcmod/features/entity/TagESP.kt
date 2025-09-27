package net.wapic.wpcmod.features.entity

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.commands.TagCommand
import net.wapic.wpcmod.util.EntityUtils
import net.wapic.wpcmod.util.render.RenderUtils

class TagESP {

	private val config get() = WpcMod.config.general.esp.tag

	init {
		WorldRenderEvents.END.register(::onRenderWorld)
	}

	fun onRenderWorld(worldRenderContext: WorldRenderContext) {
		worldRenderContext.world().entities.forEach { entity ->
			if (TagCommand.players.contains(entity.name.string.lowercase()) || EntityUtils.isTagged(entity)) {
				if (config.box) RenderUtils.drawBoundingBox(
					worldRenderContext,
					entity.boundingBox,
					config.color.getEffectiveColour()
				)
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
}