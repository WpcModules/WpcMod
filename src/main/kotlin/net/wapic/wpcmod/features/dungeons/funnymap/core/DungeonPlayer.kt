package net.wapic.wpcmod.features.dungeons.funnymap.core

import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.PlayerSkin
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.Room
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.DungeonScan
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

	private var mapLastX = 0
	private var mapLastY = 0
	private var lastYaw = 0f
	private var mapX = 0
	private var mapY = 0
	private var yaw = 0f

	fun getX(tickProgress: Float = 1f): Float {
		if(isPlayer) MC.player?.let { return ((it.x - DungeonScan.START_X + 13) * MapUtils.coordMultiplier + MapUtils.startCorner.first).toFloat() }
		MC.world?.players()?.find { it.stringUUID == uuid }?.let { return ((it.x - DungeonScan.START_X + 13) * MapUtils.coordMultiplier + MapUtils.startCorner.first).toFloat() }
		return Mth.lerp(tickProgress, mapLastX.toFloat(), mapX.toFloat())
	}
	fun getY(tickProgress: Float = 1f): Float {
		if(isPlayer) MC.player?.let { return ((it.z - DungeonScan.START_Z + 13) * MapUtils.coordMultiplier + MapUtils.startCorner.second).toFloat() }
		MC.world?.players()?.find { it.stringUUID == uuid }?.let { return ((it.z - DungeonScan.START_Z + 13) * MapUtils.coordMultiplier + MapUtils.startCorner.second).toFloat() }
		return Mth.lerp(tickProgress, mapLastY.toFloat(), mapY.toFloat())
	}
	fun getYaw(tickProgress: Float = 1f): Float {
		if(isPlayer) MC.player?.let { return it.yRot }
		MC.world?.players()?.find { it.stringUUID == uuid }?.let { return it.yRot }
		return Mth.lerp(tickProgress, lastYaw, yaw)
	}

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
	fun setData(player: Player) {
		uuid = player.stringUUID
		playerLoaded = true
	}

	fun updatePosition(rotation: Byte, x: Byte, y: Byte) {
		mapLastX = mapX
		mapLastY = mapY
		lastYaw = yaw
		yaw = rotation * 22.5f
		mapX = (x + 128) shr 1
		mapY = (y + 128) shr 1
	}


	/** Gets the player's room, used for room tracker */
	fun getCurrentRoom(): String {
		if (dead) return "Dead"
		if (DungeonUtils.bossSpawned) return "Boss"
		val x = (mapX - MapUtils.startCorner.first) / (MapUtils.roomSize + MapUtils.CONNECTOR_SIZE)
		val z = (mapY - MapUtils.startCorner.second) / (MapUtils.roomSize + MapUtils.CONNECTOR_SIZE)
		return (FunnyMap.Info.dungeonList.getOrNull(x * 2 + z * 22) as? Room)?.data?.name ?: "Error"
	}
}