package net.wapic.wpcmod.features.end

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.util.profiling.Profiler
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.boss.enderdragon.EnderDragon
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.AABB
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.features.entity.EspFeature
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.WorldRenderContext
import net.wapic.wpcmod.util.render.state.EspRenderState

object EndESP : EspFeature() {

	private var endNodes: MutableSet<AABB> = mutableSetOf()
	private val config get() = WpcMod.config.end.esp

	fun init() {
		WorldRenderEvent.EVENT.register(::renderWorld)
		ClientTickEvents.END_LEVEL_TICK.register(::worldTick)
	}

	private fun worldTick(world: ClientLevel) {
		if (Utils.getLocation() != Island.END) return
		if (!config.endNode.tracer && !config.endNode.box) return

		val player = MC.player ?: return

		val profiler = Profiler.get()
		profiler.push("end-node-tick")

		val radius = config.endNode.radius.toDouble()
		val newEndNodes: MutableSet<AABB> = mutableSetOf()
		val box = AABB.unitCubeFromLowerCorner(player.position()).inflate(radius)

		BlockPos.betweenClosed(box).forEach { blockPos ->
			if (world.getBlockState(blockPos).block == Blocks.DYED_TERRACOTTA.purple) {
				newEndNodes.add(AABB(blockPos))
			}
		}

		endNodes = newEndNodes
		profiler.pop()
	}

	private fun renderWorld(worldRenderContext: WorldRenderContext) {
		if (Utils.getLocation() != Island.END) return
		if (!config.endNode.box && !config.endNode.tracer) return

		worldRenderContext.profiler.push("end-node-esp")
		for (node in endNodes) {
			with(config.endNode) {
				if (box) worldRenderContext.drawBoundingBox(node, color)
				if (tracer) worldRenderContext.drawTracer(node.center, color)
			}
		}
		worldRenderContext.profiler.pop()
	}

	override fun compute(entity: Entity): EspRenderState? {
		if(entity is EnderDragon) return EspRenderState.fromEntity(entity, config.dragon)
		return null
	}
	override fun isEnabled(): Boolean {
		return Utils.getLocation() == Island.END && (config.dragon.glow || config.dragon.tracer || config.dragon.box)
	}
}