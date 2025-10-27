package net.wapic.wpcmod.features.dungeons.funnymap.core

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.SkinTextures
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.Room
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.FunnyMap
import net.wapic.wpcmod.features.dungeons.funnymap.utils.MapUtils
import net.wapic.wpcmod.util.DungeonUtils

data class DungeonPlayer(val skin: SkinTextures) {

	var name = ""

	/** Minecraft formatting code for the player's name */
	var colorPrefix = 'f'

	/** The player's name with formatting code */
	val formattedName: String
		get() = "§$colorPrefix$name"

	var mapX = 0
	var mapZ = 0
	var yaw = 0f

	/** Has information from player entity been loaded */
	var playerLoaded = false
	var icon = ""
	var dead = false
	var uuid = ""
	var isPlayer = false

	/** Stats for compiling player tracker information */
	var lastRoom = ""
	var lastTime = 0L
	var roomVisits: MutableList<Pair<Long, String>> = mutableListOf()

	/** Set player data that requires entity to be loaded */
	fun setData(player: PlayerEntity) {
		uuid = player.uuidAsString
		playerLoaded = true
	}

	/** Gets the player's room, used for room tracker */
	fun getCurrentRoom(): String {
		if (dead) return "Dead"
		if (DungeonUtils.bossSpawned) return "Boss"
		val x = (mapX - MapUtils.startCorner.first) / (MapUtils.roomSize + MapUtils.CONNECTOR_SIZE)
		val z = (mapZ - MapUtils.startCorner.second) / (MapUtils.roomSize + MapUtils.CONNECTOR_SIZE)
		return (FunnyMap.Info.dungeonList.getOrNull(x * 2 + z * 22) as? Room)?.data?.name ?: "Error"
	}
}
