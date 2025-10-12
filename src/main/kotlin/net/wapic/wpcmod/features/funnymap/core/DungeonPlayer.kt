package net.wapic.wpcmod.features.funnymap.core

import net.minecraft.client.util.SkinTextures
import net.minecraft.entity.player.PlayerEntity
import net.wapic.wpcmod.features.funnymap.core.map.Room
import net.wapic.wpcmod.features.funnymap.dungeon.Dungeon
import net.wapic.wpcmod.features.funnymap.utils.MapUtils
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
	var renderHat = false
	var dead = false
	var uuid = ""
	var isPlayer = false

	/** Stats for compiling player tracker information */
	var lastRoom = ""
	var lastTime = 0L
	var roomVisits: MutableList<Pair<Long, String>> = mutableListOf()

	/** Set player data that requires entity to be loaded */
	fun setData(player: PlayerEntity) {
		renderHat = false // TODO: Should render hat?
		uuid = player.uuidAsString
		playerLoaded = true
	}

	/** Gets the player's room, used for room tracker */
	fun getCurrentRoom(): String {
		if (dead) return "Dead"
		if (DungeonUtils.isBossSpawned()) return "Boss"
		val x = (mapX - MapUtils.startCorner.first) / (MapUtils.roomSize + MapUtils.CONNECTOR_SIZE)
		val z = (mapZ - MapUtils.startCorner.second) / (MapUtils.roomSize + MapUtils.CONNECTOR_SIZE)
		return (Dungeon.Info.dungeonList.getOrNull(x * 2 + z * 22) as? Room)?.data?.name ?: "Error"
	}
}
