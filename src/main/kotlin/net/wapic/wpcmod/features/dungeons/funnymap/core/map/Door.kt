package net.wapic.wpcmod.features.dungeons.funnymap.core.map

import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.dungeons.funnymap.ui.MapElement
import java.awt.Color

class Door(override val x: Int, override val z: Int, var type: DoorType) : Tile {
	private val config get() = WpcMod.config.dungeon.funnyMap.colorConfig

	var opened = false
	override var state: RoomState = RoomState.UNDISCOVERED
	override val color: Color
		get() = if (MapElement.legitRender && state == RoomState.UNOPENED) config.colorUnopenedDoor.getEffectiveColour()
		else when (type) {
			DoorType.BLOOD -> config.colorBloodDoor.getEffectiveColour()
			DoorType.ENTRANCE -> config.colorEntranceDoor.getEffectiveColour()
			DoorType.WITHER -> if (opened) config.colorOpenWitherDoor.getEffectiveColour() else config.colorWitherDoor.getEffectiveColour()
			else -> config.colorRoomDoor.getEffectiveColour()
		}
}
