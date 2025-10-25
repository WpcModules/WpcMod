package net.wapic.wpcmod.features.kuudra

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.entity.Entity
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.entity.MobGlow
import net.wapic.wpcmod.features.entity.MobGlowCache
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.KuudraUtils
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.drawBoundingBox
import net.wapic.wpcmod.util.render.drawTracer

object KuudraESP : MobGlowCache() {
	private val config get() = WpcMod.config.kuudra.esp

	fun init() {
		WorldRenderEvents.END.register(::onRenderWorld)
	}

	fun onRenderWorld(worldRenderContext: WorldRenderContext) {
		KuudraUtils.kuudraEntity?.let {
			if (config.kuudra.box)
				worldRenderContext.drawBoundingBox(it.boundingBox, config.kuudra.color.getEffectiveColour())
			if (config.kuudra.tracer)
				worldRenderContext.drawTracer(it.boundingBox.center, config.kuudra.color.getEffectiveColour())
		}
	}

	override fun compute(entity: Entity): Int {
		if (config.kuudra.glow && entity == KuudraUtils.kuudraEntity) {
			return config.kuudra.color.getEffectiveColourRGB()
		}
		return MobGlow.NO_GLOW
	}

	override fun isEnabled(): Boolean {
		return Utils.getLocation() == Island.KUUDRA
	}
}