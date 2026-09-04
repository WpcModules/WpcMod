package net.wapic.wpcmod.features.dungeons

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.ambient.Bat
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.phys.AABB
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.components.NonGlowableESPConfig
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.RoomState
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.FunnyMap
import net.wapic.wpcmod.features.entity.EspFeature
import net.wapic.wpcmod.util.HeadTextures
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.util.dungeons.DungeonUtils
import net.wapic.wpcmod.util.getNearbyArmorStands
import net.wapic.wpcmod.util.headTexture
import net.wapic.wpcmod.util.render.WpcModExtractionContext
import net.wapic.wpcmod.util.render.darker
import net.wapic.wpcmod.util.render.state.EntityState

object DungeonESP : EspFeature() {

	private val config get() = WpcMod.config.dungeon.esp
	private val miniBosses: List<String> = listOf("Lost Adventurer", "Shadow Assassin", "Diamond Guy")

	fun init() {
		WorldRenderEvent.EVENT.register(::renderWorld)
	}

	private fun renderWorld(extractor: WpcModExtractionContext, profiler: ProfilerFiller) {
		if (!isEnabled()) return
		if (!config.witherDoor.enabled) return

		profiler.push("wither-door")
		for (door in FunnyMap.espDoors) {
			if (!config.witherDoor.showAll && door.state == RoomState.UNDISCOVERED) continue
			val color = if (FunnyMap.Info.keys > 0 && door.state != RoomState.UNDISCOVERED) config.witherDoor.hasKeyColor else config.witherDoor.noKeyColor
			extractor.filledAABB(AABB(door.x - 1.0, 69.0, door.z - 1.0, door.x + 2.0, 73.0, door.z + 2.0), color.darker(), color)
		}
		profiler.pop()
	}

	fun isStarredMob(entity: Entity): Boolean {
		val armorStands = entity.getNearbyArmorStands()
		return armorStands.firstOrNull { it.name.string.matches(Regex(".*✯ .+")) } != null // Regex matching to prevent false positives from damage splash
	}

	override fun compute(entity: Entity): EntityState? {
		return when {
			entity is ArmorStand && entity.headTexture.equalsOneOf(
				HeadTextures.WITHER_KEY,
				HeadTextures.BLOOD_KEY
			) && !entity.isMarker -> EntityState(config.doorKeys, .8f, .8f, 1.35f)
			isStarredMob(entity) -> EntityState(config.starMob)
			entity.name.string in miniBosses -> EntityState(config.miniboss)
			entity is Bat && !entity.isInvisible && entity.health == 100f -> EntityState(config.bat)
			else -> null
		}
	}

	override fun isEnabled(): Boolean = DungeonUtils.inDungeons && !DungeonUtils.bossSpawned

	data class DoorConfig(override var color: ChromaColour) : NonGlowableESPConfig() {

		override var box: Boolean = config.witherDoor.enabled
	}
}