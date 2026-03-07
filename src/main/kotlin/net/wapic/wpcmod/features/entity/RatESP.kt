package net.wapic.wpcmod.features.entity

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.util.EntityUtils.headTexture
import net.wapic.wpcmod.util.HeadTextures
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.WorldRenderContext

object RatESP : MobGlowCache() {

	private val config get() = WpcMod.config.general.esp.rat

	fun init() {
		WorldRenderEvent.EVENT.register(::onRenderWorld)
	}

	private fun isRat(entity: ArmorStand?): Boolean = entity?.headTexture == HeadTextures.RAT

	private fun onRenderWorld(worldRenderContext: WorldRenderContext) {
		if(!isEnabled()) return
		worldRenderContext.profiler.push("rat-esp")
		val entities = MC.entitiesOf<ArmorStand>().filter(::isRat)
		val tickDelta = worldRenderContext.tickCounter.getGameTimeDeltaPartialTick(true)

		for (entity in entities) {
			val pos = entity.getEyePosition(tickDelta)
			if (config.box) worldRenderContext.drawBoundingBox(pos, entity.bbWidth, entity.bbWidth, config.color)
			if (config.tracer) worldRenderContext.drawTracer(pos, config.color)
		}
		worldRenderContext.profiler.pop()
	}

	override fun compute(entity: Entity): ChromaColour? =
		config.color.takeIf { config.glow && isRat(entity as? ArmorStand) }

	override fun isEnabled(): Boolean = Utils.getLocation() == Island.HUB && (config.tracer || config.box)
}