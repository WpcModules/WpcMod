package net.wapic.wpcmod.features.mining

import net.minecraft.core.Position
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
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

		val blockEntities = Utils.getLoadedBlockEntities().filterIsInstance<ChestBlockEntity>()

		val playerPos = worldRenderContext.camera.pos

		blockEntities.forEach {
			if(playerPos.distanceTo(Vec3.atCenterOf(it.blockPos)) >= config.radius) return@forEach

			val chest = AABB(it.blockPos)

			if (config.box)
				worldRenderContext.drawBoundingBox(chest, config.color)
			if (config.tracer)
				worldRenderContext.drawTracer(chest.center, config.color, config.tracerWidth)
		}

		worldRenderContext.profiler.pop()
	}
}