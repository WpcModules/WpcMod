package net.wapic.wpcmod.features.end

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.boss.dragon.EnderDragonEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.features.entity.MobGlowCache
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.WorldRenderContext

object EndESP : MobGlowCache() {

	private var endNodes: MutableSet<Box> = mutableSetOf()
	private val config get() = WpcMod.config.end.esp

	fun init() {
		WorldRenderEvent.EVENT.register(::renderWorld)
		ClientTickEvents.END_WORLD_TICK.register(::worldTick)
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
			val pos = it.entityPos
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

		val profiler = worldRenderContext.profiler()
		profiler.push("end-esp")

		MC.world?.entities?.forEach { entity ->
			val settings = when (entity) {
				is EnderDragonEntity -> config.dragon
				else -> return@forEach
			}

			if (settings.box)
				worldRenderContext.drawBoundingBox(entity.boundingBox, settings.color)
			if (settings.tracer)
				worldRenderContext.drawTracer(
					entity.boundingBox.center,
					settings.color
				)
		}

		profiler.swap("end-nodes")
		if(config.endNode.box || config.endNode.tracer) {
			for (node in endNodes) {
				if (config.endNode.box)
					worldRenderContext.drawBoundingBox(node, config.endNode.color)
				if (config.endNode.tracer)
					worldRenderContext.drawTracer(node.center, config.endNode.color)
			}
		}
		profiler.pop()
	}

	override fun compute(entity: Entity): ChromaColour? {
		return when {
			config.dragon.glow && entity is EnderDragonEntity -> config.dragon.color
			else -> null
		}
	}

	override fun isEnabled(): Boolean {
		return Utils.getLocation() == Island.END
	}
}