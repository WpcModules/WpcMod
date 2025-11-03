package net.wapic.wpcmod.features.mining

import net.minecraft.util.math.Box
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.WorldRenderContext

object ChestESP {

	private val config get() = WpcMod.config.mining.esp.chest

	fun init() {
		WorldRenderEvent.EVENT.register(::renderWorld)
	}

	private fun renderWorld(worldRenderContext: WorldRenderContext) {
		if (Utils.getLocation() != Island.CRYSTAL_HOLLOWS) return
		if (!config.tracer && !config.box) return
		worldRenderContext.profiler.push("chest-esp")

		val blockEntities = worldRenderContext.world.blockEntities

		val playerPos = worldRenderContext.camera.pos

		blockEntities.forEach {
			if(playerPos.distanceTo(it.pos.toCenterPos()) >= config.radius) return@forEach

			val chest = Box.of(it.pos.toCenterPos(), 1.0, 1.0, 1.0)

			if (config.box)
				worldRenderContext.drawBoundingBox(chest, config.color)
			if (config.tracer)
				worldRenderContext.drawTracer(chest.center, config.color)
		}

		worldRenderContext.profiler.pop()
	}
}