package net.wapic.wpcmod.features.dungeons

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.ambient.Bat
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.AABB
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.RoomState
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.FunnyMap
import net.wapic.wpcmod.features.entity.MobGlowCache
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.EntityUtils.getArmorStandsByEntity
import net.wapic.wpcmod.util.EntityUtils.headTexture
import net.wapic.wpcmod.util.HeadTextures
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.util.render.WorldRenderContext
import net.wapic.wpcmod.util.render.brighter
import net.wapic.wpcmod.util.render.darker

object DungeonESP : MobGlowCache() {

	private val config get() = WpcMod.config.dungeon.esp
	private val miniBosses: List<String> = listOf("Lost Adventurer", "Shadow Assassin", "Diamond Guy")

	fun init() {
		WorldRenderEvent.EVENT.register(::renderWorld)
	}

	private fun renderWorld(worldRenderContext: WorldRenderContext) {
		if(!isEnabled()) return
		worldRenderContext.profiler.push("dungeon-esp")

		for (entity in worldRenderContext.world.entitiesForRendering()) {
			val entityConfig = when {
				entity is ArmorStand && entity.headTexture.equalsOneOf(
					HeadTextures.BLOOD_KEY,
					HeadTextures.WITHER_KEY
				) && !entity.isMarker -> config.doorKeys
				isStarredMob(entity) || (entity is ArmorStand && entity.headTexture == HeadTextures.FEL) -> config.starMob
				entity is Bat && !entity.isInvisible && entity.health == 100f -> config.bat
				entity is Player && entity.name.string in miniBosses -> config.miniboss
				else -> continue
			}

			if(entityConfig.box) {
				worldRenderContext.drawBoundingBox(entity.boundingBox, entityConfig.color)
			}
			if(entityConfig.tracer) {
				val pos = if (entity is ArmorStand) entity.eyePosition else entity.boundingBox.center
				worldRenderContext.drawTracer(pos, entityConfig.color)
			}
		}

		worldRenderContext.profiler.popPush("door esp")
		if (config.witherDoor.box) {
			val color = (if (FunnyMap.Info.keys > 0) config.witherDoor.hasKeyColor else config.witherDoor.noKeyColor)

			FunnyMap.espDoors.forEach { door ->
				if (door.state == RoomState.UNDISCOVERED) return@forEach
				val box = AABB(door.x - 1.0, 69.0, door.z - 1.0, door.x + 2.0, 73.0, door.z + 2.0)
				worldRenderContext.drawFilledBoxWithOutline(box, color.darker(), color.brighter(), 4.0)
			}
		}
		worldRenderContext.profiler.pop()
	}

	fun isStarredMob(entity: Entity): Boolean {
		val armorStands = getArmorStandsByEntity(entity)
		return armorStands.isNotEmpty() && armorStands.first().name?.string?.contains("✯") ?: false
	}

	override fun compute(entity: Entity): ChromaColour? {
		return when {
			config.doorKeys.glow && entity is ArmorStand && entity.headTexture.equalsOneOf(
				HeadTextures.WITHER_KEY,
				HeadTextures.BLOOD_KEY
			) && !entity.isMarker -> config.doorKeys.color
			config.starMob.glow && isStarredMob(entity) -> config.starMob.color
			config.miniboss.glow && entity.name.string in miniBosses -> config.miniboss.color
			config.bat.glow && entity is Bat && !entity.isInvisible && entity.health == 100f -> config.bat.color
			else -> null
		}
	}

	override fun isEnabled(): Boolean = DungeonUtils.inDungeons && !DungeonUtils.bossSpawned
}