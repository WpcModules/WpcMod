package net.wapic.wpcmod.features.end

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.boss.dragon.EnderDragonEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.RenderUtils

class EndESP {

	private var endNodes: MutableSet<Box> = mutableSetOf()
	private val config get() = WpcMod.config.endConfig.esp

	data class ESPSettings(var box: Boolean, var tracer: Boolean, var color: ChromaColour)

	init {
		WorldRenderEvents.END.register(::renderWorld)
		ClientTickEvents.END_WORLD_TICK.register(::worldTick)
	}

	private fun getSettings(entity: Entity): ESPSettings {
		when (entity) {
			is EnderDragonEntity -> return ESPSettings(
				config.dragon.box, config.dragon.tracer, config.dragon.color
			)
		}
		return ESPSettings(box = false, tracer = false, color = ChromaColour(1f, 1f, 1f, 0, 0xff))
	}

	private var lock = false
	private fun worldTick(world: ClientWorld) {
		if (Utils.getLocation() != Island.END) return
		if (!config.endNode.tracer && !config.endNode.box) return

		if (lock) return

		val player = MinecraftClient.getInstance().player
		val radius = config.endNode.radius.toDouble()

		val newEndNodes: MutableSet<Box> = mutableSetOf()
		lock = true

		player?.let {
			val pos = it.pos
			val box = Box.from(pos).expand(radius)

			BlockPos.iterate(box).forEach { blockPos ->
				if (world.getBlockState(blockPos).block == Blocks.PURPLE_TERRACOTTA) {
					newEndNodes.add(Box.of(blockPos.toCenterPos(), 1.0, 1.0, 1.0))
				}
			}
		}

		lock = false
		endNodes = newEndNodes
	}

	private fun renderWorld(worldRenderContext: WorldRenderContext) {
		if (Utils.getLocation() != Island.END) return

		worldRenderContext.world().entities.forEach { entity ->
			val settings = getSettings(entity)
			if (settings.box) RenderUtils.drawBoundingBox(
				worldRenderContext, entity.boundingBox, color = settings.color.getEffectiveColour()
			)
			if (settings.tracer) RenderUtils.drawTracer(
				worldRenderContext, entity.x, entity.eyeY, entity.z, color = settings.color.getEffectiveColour()
			)
		}

		endNodes.forEach { node ->
			if (config.endNode.box) RenderUtils.drawBoundingBox(
				worldRenderContext, node.withMinY(node.maxY), config.endNode.color.getEffectiveColour()
			)
			if (config.endNode.tracer) RenderUtils.drawTracer(
				worldRenderContext,
				node.center.x,
				node.maxY,
				node.center.z,
				color = config.endNode.color.getEffectiveColour()

			)
		}
	}
}