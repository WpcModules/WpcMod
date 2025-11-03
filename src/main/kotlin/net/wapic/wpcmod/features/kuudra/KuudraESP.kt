package net.wapic.wpcmod.features.kuudra

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.entity.Entity
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.features.entity.MobGlowCache
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.KuudraUtils
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.WorldRenderContext

object KuudraESP : MobGlowCache() {
	private val config get() = WpcMod.config.kuudra.esp

	fun init() {
		WorldRenderEvent.EVENT.register(::onRenderWorld)
	}

	fun onRenderWorld(worldRenderContext: WorldRenderContext) {
		if(!isEnabled()) return
		worldRenderContext.profiler.push("kuudra-esp")

		KuudraUtils.kuudraEntity?.let {
			if (config.kuudra.box)
				worldRenderContext.drawBoundingBox(it.boundingBox, config.kuudra.color)
			if (config.kuudra.tracer)
				worldRenderContext.drawTracer(it.boundingBox.center, config.kuudra.color)
		}
		worldRenderContext.profiler.pop()
	}

	override fun compute(entity: Entity): ChromaColour? {
		if (config.kuudra.glow && entity == KuudraUtils.kuudraEntity) {
			return config.kuudra.color
		}
		return null
	}

	override fun isEnabled(): Boolean {
		return Utils.getLocation() == Island.KUUDRA
	}
}