package net.wapic.wpcmod.features.dungeons.funnymap.core

import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.PlayerSkin
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.Room
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.FunnyMap
import net.wapic.wpcmod.features.dungeons.funnymap.utils.MapUtils
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.MC

data class DungeonPlayer(val skin: PlayerSkin) {

	var name = ""

	/** Minecraft formatting code for the player's name */
	var colorPrefix = 'f'

	/** The player's name with formatting code */
	val formattedName: String
		get() = "§$colorPrefix$name"

	var dungeonClass: DungeonUtils.DungeonClass = DungeonUtils.DungeonClass.EMPTY

	var mapX = 0f
	var mapZ = 0f
	var yaw = 0f

	var lastMapX = 0f
	var lastMapZ = 0f
	var lastYaw = 0f

	/** Has information from player entity been loaded */
	var playerLoaded = false
	var icon = ""
	var dead = false
	var uuid = ""
	var isPlayer = false

	/** Stats for compiling player tracker information */
	var lastRoom: Room? = null
	var lastTime = 0L
	var roomVisits: MutableList<Pair<Long, Room>> = mutableListOf()

	/** Set player data that requires entity to be loaded */
	fun setData(player: Player) {
		uuid = player.stringUUID
		isPlayer = uuid == MC.player?.stringUUID
		playerLoaded = true
	}

	fun updatePos(x: Float, z: Float, yRot: Float) {
		lastMapX = mapX
		lastMapZ = mapZ
		lastYaw = yaw
		mapX = x
		mapZ = z
		yaw = yRot
	}

	/** Gets the player's room, used for room tracker */
	fun getCurrentRoom(): Room? {
		if (dead) return null
		if (DungeonUtils.bossSpawned) return null
		val x = ((mapX - MapUtils.startCorner.first) / (MapUtils.roomSize + MapUtils.CONNECTOR_SIZE)).toInt()
		val z = ((mapZ - MapUtils.startCorner.second) / (MapUtils.roomSize + MapUtils.CONNECTOR_SIZE)).toInt()
		return (FunnyMap.Info.dungeonList.getOrNull(x * 2 + z * 22) as? Room)
	}
}
