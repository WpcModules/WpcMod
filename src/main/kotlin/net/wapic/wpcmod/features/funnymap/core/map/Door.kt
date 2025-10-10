package net.wapic.wpcmod.features.funnymap.core.map

import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.funnymap.features.dungeon.MapRender
import java.awt.Color

class Door(override val x: Int, override val z: Int, var type: DoorType) : Tile {
	val config get() = WpcMod.config.funnyMap

	var opened = false
	override var state: RoomState = RoomState.UNDISCOVERED
	override val color: Color
		get() = if (MapRender.legitRender && state == RoomState.UNOPENED) config.colorUnopenedDoor.getEffectiveColour()
		else when (type) {
			DoorType.BLOOD -> config.colorBloodDoor.getEffectiveColour()
			DoorType.ENTRANCE -> config.colorEntranceDoor.getEffectiveColour()
			DoorType.WITHER -> if (opened) config.colorOpenWitherDoor.getEffectiveColour() else config.colorWitherDoor.getEffectiveColour()
			else -> config.colorRoomDoor.getEffectiveColour()
		}
}
