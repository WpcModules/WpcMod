package net.wapic.wpcmod.features.end

import net.minecraft.core.BlockPos
import net.minecraft.util.profiling.ProfilerFiller
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
import net.wapic.wpcmod.util.render.WpcModExtractionContext
import net.wapic.wpcmod.util.render.state.EntityState

object EndESP : EspFeature() {

	private val config get() = WpcMod.config.end.esp

	fun init() {
		WorldRenderEvent.EVENT.register(::renderWorld)
	}

	private fun renderWorld(context: WpcModExtractionContext, profiler: ProfilerFiller) {
		if (Utils.getLocation() != Island.END) return
		profiler.push("endNodes")
		val player = MC.player ?: return
		val radius = config.endNode.radius.toDouble()
		val box = AABB.unitCubeFromLowerCorner(player.position()).inflate(radius)

		BlockPos.betweenClosed(box).forEach { blockPos ->
			if (context.level.getBlockState(blockPos).block != Blocks.DYED_TERRACOTTA.purple) return@forEach
			context.blockESP(blockPos, config.endNode)
		}
		profiler.pop()
	}

	override fun compute(entity: Entity): EntityState? {
		if (entity is EnderDragon) return EntityState(config.dragon)
		return null
	}

	override fun isEnabled(): Boolean {
		return Utils.getLocation() == Island.END && (config.dragon.glow || config.dragon.tracer || config.dragon.box)
	}
}