package net.wapic.wpcmod.features.foraging

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.Display
import net.minecraft.world.item.Items
import net.minecraft.world.phys.AABB
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.WorldRenderContext

object ForestNodeESP {

	private val config get() = WpcMod.config.foraging.esp.forestNode
	private var forestNodes: List<AABB>? = null

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
		WorldRenderEvent.EVENT.register(::renderWorld)
		WorldChangeEvent.AFTER.register { forestNodes = null }
	}

	fun onTick(client: Minecraft) {
		if (!isEnabled()) return
		forestNodes = MC.entitiesOf<Display.ItemDisplay>()
			.filter { it.itemStack.item == Items.STRING }
			.distinctBy { it.blockPosition() }
			.map { AABB(it.blockPosition()) }
	}

	private fun renderWorld(worldRenderContext: WorldRenderContext) {
		if (!isEnabled()) return
		worldRenderContext.profiler.push("forest-node-esp")
		forestNodes?.forEach { node ->
			if (config.box) worldRenderContext.drawBoundingBox(node.setMinY(node.maxY), config.color)
			if (config.tracer) worldRenderContext.drawTracer(
				node.setMinY(node.maxY).center,
				config.color,
				config.tracerWidth
			)
		}
		worldRenderContext.profiler.pop()
	}

	private fun isEnabled(): Boolean {
		if (!config.enabledIslands.any { it.island == Utils.getLocation() }) return false
		if (!config.box && !config.tracer) return false
		return true
	}

	enum class ForestNodeIslands(val displayName: String, val island: Island) {
		TORRHUS_CANYON("Torrhus Canyon", Island.TORRHUS_CANYON),
		GALATEA("Galatea", Island.GALATEA),
		SAFARI("Safari Zone", Island.SAFARI);

		override fun toString(): String = displayName
	}
}