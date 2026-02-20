package net.wapic.wpcmod.features.dungeons.funnymap.core.map

import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.FunnyMap

class UniqueRoom(arrX: Int, arrY: Int, room: Room) {
	private val config get() = WpcMod.config.dungeon.funnyMap

	var name: String
	var topLeft = Pair(arrX, arrY)
	private var center = Pair(arrX, arrY)
	var mainRoom = room
		get() = FunnyMap.Info.dungeonList[topLeft.second * 11 + topLeft.first] as? Room ?: field
	val tiles = mutableListOf(room to Pair(arrX, arrY))
	var hasMimic = false
	var hasInitialized = false

	init {
		if (room.data.name == "Unknown") {
			name = "Unknown_${arrX}_${arrY}"
		} else {
			name = room.data.name
			init(room)
		}
		room.uniqueRoom = this
		FunnyMap.Info.uniqueRooms.add(this)
	}

	fun init(room: Room) {
		// I'm not sure why on rare occasions rooms get initialized twice but this should fix that for now
		if (hasInitialized) return WpcMod.logger.error("Tried to initialize \"${room.data.name}\" but it is already initialized")

		FunnyMap.Info.cryptCount += room.data.crypts
		FunnyMap.Info.secretCount += room.data.secrets

		when (room.data.type) {
			RoomType.TRAP -> FunnyMap.Info.trapType = room.data.name.split(" ")[0]
			RoomType.PUZZLE -> Puzzle.fromName(room.data.name)?.let { FunnyMap.Info.puzzles.putIfAbsent(it, false) }
			else -> {}
		}

		hasInitialized = true
	}

	fun addTile(x: Int, y: Int, tile: Room) {
		addToTiles(x, y, tile)
		calculateCenter()
	}

	fun addTiles(tiles: Iterable<Pair<Int, Int>>) {
		tiles.forEach { (x, y) ->
			val room = FunnyMap.Info.dungeonList[y * 11 + x] as? Room ?: return@forEach
			if (room.uniqueRoom !== this) {
				FunnyMap.Info.uniqueRooms.remove(room.uniqueRoom)
				addToTiles(x, y, room)
			}
		}
		calculateCenter()
	}

	private fun addToTiles(x: Int, y: Int, tile: Room) {
		if (mainRoom.data.name == "Unknown") {
			if (tile.data.name != "Unknown") {
				init(tile)
				name = tile.data.name
				mainRoom.data = tile.data
			}
		} else if (tile.data.name == "Unknown") {
			tile.data = mainRoom.data
		}

		tile.uniqueRoom = this

		tiles.removeIf { it.first.x == tile.x && it.first.z == tile.z }
		tiles.add(tile to Pair(x, y))

		if (x < topLeft.first || (x == topLeft.first && y < topLeft.second)) {
			topLeft = Pair(x, y)
			mainRoom = tile
			if (name.startsWith("Unknown")) {
				name = "Unknown_${x}_${y}"
			}
		}
	}

	private fun calculateCenter() {
		if (tiles.size == 1) {
			center = tiles.first().second
			return
		}

		val positions = tiles.mapNotNull {
			it.second.takeIf { (arrX, arrZ) ->
				arrX % 2 == 0 && arrZ % 2 == 0
			}
		}

		if (positions.isEmpty()) return

		val xRooms = positions.groupBy { it.first }.entries.sortedByDescending { it.value.size }
		val zRooms = positions.groupBy { it.second }.entries.sortedByDescending { it.value.size }

		center = when {
			zRooms.size == 1 || zRooms[0].value.size != zRooms[1].value.size -> {
				xRooms.sumOf { it.key } / xRooms.size to zRooms[0].key
			}

			xRooms.size == 1 || xRooms[0].value.size != xRooms[1].value.size -> {
				xRooms[0].key to zRooms.sumOf { it.key } / zRooms.size
			}

			else -> (xRooms[0].key + xRooms[1].key) / 2 to (zRooms[0].key + zRooms[1].key) / 2
		}
	}

	fun getNamePosition(): Pair<Int, Int> = if (config.mapCenterRoomName) center else topLeft

	fun getCheckmarkPosition(): Pair<Int, Int> = if (config.mapCenterCheckmark) center else topLeft
}
