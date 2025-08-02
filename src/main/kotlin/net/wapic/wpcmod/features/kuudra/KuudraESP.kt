package net.wapic.wpcmod.features.kuudra

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.KuudraUtils
import net.wapic.wpcmod.util.render.RenderUtils

class KuudraESP {
	private val config get() = WpcMod.config.kuudraConfig.esp

	init {
		WorldRenderEvents.LAST.register(::onRenderWorld)
	}

	fun onRenderWorld(worldRenderContext: WorldRenderContext) {
		if (config.kuudra.tracer) {
			KuudraUtils.kuudraEntity?.let {
				RenderUtils.drawTracer(worldRenderContext, it.x, it.y, it.z, config.kuudra.color.getEffectiveColour())
			}
		}
	}
}