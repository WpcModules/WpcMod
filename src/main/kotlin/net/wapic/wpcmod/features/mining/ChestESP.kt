package net.wapic.wpcmod.features.mining

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.util.math.Box
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.drawBoundingBox
import net.wapic.wpcmod.util.render.drawTracer

class ChestESP {

	private val config get() = WpcMod.config.mining.esp.chest

	init {
		WorldRenderEvents.END.register(::renderWorld)
	}

	private fun renderWorld(worldRenderContext: WorldRenderContext) {
		if (Utils.getLocation() != Island.CRYSTAL_HOLLOWS) return
		if (!config.tracer && !config.box) return

		val blockEntities = Utils.getLoadedBlockEntities().filterIsInstance<ChestBlockEntity>()
		val tickProgress: Float = worldRenderContext.tickCounter().dynamicDeltaTicks
		val playerPos = worldRenderContext.camera().pos

		for (block in blockEntities) {
			if (playerPos.distanceTo(block.pos.toCenterPos()) >= config.radius) continue
			if (block.getAnimationProgress(tickProgress) > 0) continue

			val chest = Box.of(block.pos.toCenterPos(), 1.0, 1.0, 1.0)

			if (config.box)
				worldRenderContext.drawBoundingBox(chest, config.color.getEffectiveColour())
			if (config.tracer)
				worldRenderContext.drawTracer(chest.center, config.color.getEffectiveColour())
		}
	}
}