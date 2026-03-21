package net.wapic.wpcmod.features.dungeons

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.ambient.Bat
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.phys.AABB
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.components.GlowableESPConfig
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.RoomState
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.FunnyMap
import net.wapic.wpcmod.features.entity.EspFeature
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.EntityUtils.getArmorStandsByEntity
import net.wapic.wpcmod.util.EntityUtils.headTexture
import net.wapic.wpcmod.util.HeadTextures
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.util.render.WorldRenderContext
import net.wapic.wpcmod.util.render.brighter
import net.wapic.wpcmod.util.render.darker

object DungeonESP : EspFeature() {

	private val config get() = WpcMod.config.dungeon.esp
	private val miniBosses: List<String> = listOf("Lost Adventurer", "Shadow Assassin", "Diamond Guy")

	fun init() {
		WorldRenderEvent.EVENT.register(::renderWorld)
	}

	private fun renderWorld(worldRenderContext: WorldRenderContext) {
		if(!isEnabled()) return

		worldRenderContext.profiler.push("wither-door-esp")
		if (config.witherDoor.box) {

			for (door in FunnyMap.espDoors) {
				if (!config.witherDoor.showAll && door.state == RoomState.UNDISCOVERED) continue
				val color =
					if (FunnyMap.Info.keys > 0 && door.state != RoomState.UNDISCOVERED) config.witherDoor.hasKeyColor else config.witherDoor.noKeyColor
				val box = AABB(door.x - 1.0, 69.0, door.z - 1.0, door.x + 2.0, 73.0, door.z + 2.0)
				worldRenderContext.drawFilledBoxWithOutline(box, color.darker(), color.brighter(), 4f)
			}
		}
		worldRenderContext.profiler.pop()
	}

	fun isStarredMob(entity: Entity): Boolean {
		val armorStands = getArmorStandsByEntity(entity)
		return armorStands.isNotEmpty() && armorStands.first().name.string.contains("✯")
	}

	override fun compute(entity: Entity): GlowableESPConfig? {
		return when {
			config.doorKeys.glow && entity is ArmorStand && entity.headTexture.equalsOneOf(
				HeadTextures.WITHER_KEY,
				HeadTextures.BLOOD_KEY
			) && !entity.isMarker -> config.doorKeys
			isStarredMob(entity) -> config.starMob
			entity.name.string in miniBosses -> config.miniboss
			entity is Bat && !entity.isInvisible && entity.health == 100f -> config.bat
			else -> null
		}
	}

	override fun isEnabled(): Boolean = DungeonUtils.inDungeons && !DungeonUtils.bossSpawned
}