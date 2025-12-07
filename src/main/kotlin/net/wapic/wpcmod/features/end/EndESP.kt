package net.wapic.wpcmod.features.end

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.boss.dragon.EnderDragonEntity
import net.minecraft.util.math.Box
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.features.entity.MobGlowCache
import net.wapic.wpcmod.util.BFSProcessor
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.WorldRenderContext

object EndESP : MobGlowCache() {
	private val processor = BFSProcessor
	private var endNodes: MutableSet<Box> = mutableSetOf()
	private val config get() = WpcMod.config.end.esp

	fun init() {
		WorldRenderEvent.EVENT.register(::renderWorld)
		ClientTickEvents.END_WORLD_TICK.register(::worldTick)
	}

	private fun worldTick(world: ClientWorld) {
		if (Utils.getLocation() != Island.END) return
		if (!config.endNode.tracer && !config.endNode.box) return

		val player = MinecraftClient.getInstance().player
		val radius = config.endNode.radius.toInt()
		val maxSteps = config.endNode.blocksPerTick.toInt()

		val newEndNodes: MutableSet<Box> = mutableSetOf()

		player?.let {
			if (processor.done()) {
				processor.blocks().forEach { blockPos ->
					newEndNodes.add(Box.of(blockPos.toCenterPos(), 1.0, 1.0, 1.0))
				}
				endNodes = newEndNodes

				processor.start(world, it.blockPos, Blocks.PURPLE_TERRACOTTA, radius, maxSteps)
			} else {
				processor.tick()
			}
		}
	}

	private fun renderWorld(worldRenderContext: WorldRenderContext) {
		if (Utils.getLocation() != Island.END) return

		for (entity in worldRenderContext.world.entities) {
			val settings = when (entity) {
				is EnderDragonEntity -> config.dragon
				else -> continue
			}

			if (settings.box)
				worldRenderContext.drawBoundingBox(entity.boundingBox, settings.color)
			if (settings.tracer)
				worldRenderContext.drawTracer(
					entity.boundingBox.center,
					settings.color
				)
		}

		if(config.endNode.box || config.endNode.tracer) {
			for (node in endNodes) {
				if (config.endNode.box)
					worldRenderContext.drawBoundingBox(node, config.endNode.color)
				if (config.endNode.tracer)
					worldRenderContext.drawTracer(node.center, config.endNode.color)
			}
		}
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