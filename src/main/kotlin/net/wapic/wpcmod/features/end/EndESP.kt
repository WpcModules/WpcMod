package net.wapic.wpcmod.features.end

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.boss.enderdragon.EnderDragon
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.AABB
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.features.entity.MobGlowCache
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.WorldRenderContext

object EndESP : MobGlowCache() {

	private var endNodes: MutableSet<AABB> = mutableSetOf()
	private val config get() = WpcMod.config.end.esp

	fun init() {
		WorldRenderEvent.EVENT.register(::renderWorld)
		ClientTickEvents.END_WORLD_TICK.register(::worldTick)
	}

	private fun worldTick(world: ClientLevel) {
		if (Utils.getLocation() != Island.END) return
		if (!config.endNode.tracer && !config.endNode.box) return

		val player = MC.player ?: return
		val radius = config.endNode.radius.toDouble()

		val newEndNodes: MutableSet<AABB> = mutableSetOf()

		val box = AABB.unitCubeFromLowerCorner(player.position()).inflate(radius)

		BlockPos.betweenClosed(box).forEach { blockPos ->
			if (world.getBlockState(blockPos).block == Blocks.PURPLE_TERRACOTTA) {
				newEndNodes.add(AABB.ofSize(blockPos.center, 1.0, 1.0, 1.0))
			}
		}

		endNodes = newEndNodes
	}

	private fun renderWorld(worldRenderContext: WorldRenderContext) {
		if (Utils.getLocation() != Island.END) return

		worldRenderContext.profiler.push("end-esp")

		worldRenderContext.level.entitiesForRendering().forEach { entity ->
			val settings = when (entity) {
				is EnderDragon -> config.dragon
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

		worldRenderContext.profiler.popPush("end-nodes")
		if(config.endNode.box || config.endNode.tracer) {
			for (node in endNodes) {
				if (config.endNode.box)
					worldRenderContext.drawBoundingBox(node, config.endNode.color)
				if (config.endNode.tracer)
					worldRenderContext.drawTracer(node.center, config.endNode.color)
			}
		}
		worldRenderContext.profiler.pop()
	}

	override fun compute(entity: Entity): ChromaColour? {
		return when {
			config.dragon.glow && entity is EnderDragon -> config.dragon.color
			else -> null
		}
	}

	override fun isEnabled(): Boolean {
		return Utils.getLocation() == Island.END
	}
}