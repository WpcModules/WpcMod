package net.wapic.wpcmod.features.mining

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.block.DragonEggBlock
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.util.math.Box
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.RenderUtils

class ChestESP {

	private val config get() = WpcMod.config.mining.esp

	init {
		WorldRenderEvents.END.register(::renderWorld)
	}

	private fun renderWorld(worldRenderContext: WorldRenderContext) {
		if (Utils.getLocation() != Island.CRYSTAL_HOLLOWS) return
		Utils.getLoadedBlockEntities().filterIsInstance<ChestBlockEntity>().forEach { entity ->

			val blockPos = entity.pos
			val chest = Box.of(blockPos.toCenterPos(), 1.0, 1.0, 1.0)

			if (config.chest.box) RenderUtils.drawBoundingBox(
				worldRenderContext, chest.withMinY(chest.minY), config.chest.color.getEffectiveColour()
			)
			if (config.chest.tracer) RenderUtils.drawTracer(
				worldRenderContext,
				chest.center.x,
				chest.maxY,
				chest.center.z,
				color = config.chest.color.getEffectiveColour()
			)
		}
	}
}