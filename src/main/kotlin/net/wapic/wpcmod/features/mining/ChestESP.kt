package net.wapic.wpcmod.features.mining

import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraft.world.phys.Vec3
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.WpcModExtractionContext

object ChestESP {

	private val config get() = WpcMod.config.mining.esp.chest

	fun init() {
		WorldRenderEvent.EVENT.register(::renderWorld)
	}

	private fun renderWorld(context: WpcModExtractionContext, profiler: ProfilerFiller) {
		if (Utils.getLocation() != Island.CRYSTAL_HOLLOWS) return
		if (!config.tracer && !config.box) return
		profiler.push("chest-esp")

		val blockEntities = Utils.getLoadedBlockEntities().filterIsInstance<ChestBlockEntity>()

		val playerPos = MC.cameraPos ?: return

		blockEntities.forEach {
			if (playerPos.distanceTo(Vec3.atCenterOf(it.blockPos)) >= config.radius) return@forEach
			context.blockESP(it.blockPos, config)
		}

		profiler.pop()
	}
}