package net.wapic.wpcmod.features.foraging

import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.entity.Display
import net.minecraft.world.item.Items
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.SafariAPI.SafariBiome.Companion.isSimilarTo
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.biome
import net.wapic.wpcmod.util.render.WpcModExtractionContext

object ForestNodeESP {

	private val config get() = WpcMod.config.foraging.esp.forestNode

	fun init() {
		WorldRenderEvent.EVENT.register(::renderWorld)
	}

	private fun isInBiome(entity: Display.ItemDisplay): Boolean {
		if (!config.onlyCurrentBiome) return true
		val player = MC.player ?: return false
		return entity.biome.isSimilarTo(player.biome)
	}

	private fun renderWorld(context: WpcModExtractionContext, profiler: ProfilerFiller) {
		if (!isEnabled()) return
		profiler.push("forest-node-esp")
		val nodes = context.level.entitiesForRendering().filterIsInstance<Display.ItemDisplay>()
			.filter { it.itemStack.item == Items.STRING && isInBiome(it) }
			.distinctBy { it.blockPosition() }

		for (node in nodes) {
			context.blockESP(node.blockPosition(), config)
		}

		profiler.pop()
	}

	private fun isEnabled(): Boolean {
		return config.enabledIslands.any { it.island == Utils.getLocation() } && (config.box || config.tracer)
	}

	enum class ForestNodeIslands(val displayName: String, val island: Island) {
		TORRHUS_CANYON("Torrhus Canyon", Island.TORRHUS_CANYON),
		GALATEA("Galatea", Island.GALATEA),
		SAFARI("Safari Zone", Island.SAFARI);

		override fun toString(): String = displayName
	}
}