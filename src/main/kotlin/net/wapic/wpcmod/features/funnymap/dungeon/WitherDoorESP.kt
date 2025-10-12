package net.wapic.wpcmod.features.funnymap.dungeon

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.util.math.Box
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.funnymap.core.map.RoomState
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.DungeonUtils.inDungeons
import net.wapic.wpcmod.util.render.RenderUtils

object WitherDoorESP {

	val config get() = WpcMod.config.funnyMap

	fun init() {
		WorldRenderEvents.END.register(::onRenderWorld)
	}

	fun onRenderWorld(worldRenderContext: WorldRenderContext) {
		if (!inDungeons || DungeonUtils.isBossSpawned() || !config.witherDoorESP) return
		val color = if (Dungeon.Info.keys > 0) config.witherDoorKeyColor else config.witherDoorNoKeyColor

		Dungeon.espDoors.forEach { door ->
			if (door.state == RoomState.UNDISCOVERED) return@forEach
			val box = Box(door.x - 1.0, 69.0, door.z - 1.0, door.x + 2.0, 73.0, door.z + 2.0)
			RenderUtils.drawBoundingBox(worldRenderContext, box, color.getEffectiveColour())
		}
	}
}
