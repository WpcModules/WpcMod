package net.wapic.wpcmod.features.dungeons.funnymap.core.map

import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.dungeons.funnymap.core.RoomData
import net.wapic.wpcmod.features.dungeons.funnymap.ui.MapElement
import java.awt.Color

class Room(override val x: Int, override val z: Int, var data: RoomData) : Tile {
	private val config get() = WpcMod.config.dungeon.funnyMap.colorConfig
	var core = 0
	var isSeparator = false
	var uniqueRoom: UniqueRoom? = null
	override var state: RoomState = RoomState.UNDISCOVERED
	override val color: Color
		get() = if (MapElement.legitRender && state == RoomState.UNOPENED) config.colorUnopened.getEffectiveColour()
		else when (data.type) {
			RoomType.BLOOD -> config.colorBlood.getEffectiveColour()
			RoomType.CHAMPION -> config.colorMiniboss.getEffectiveColour()
			RoomType.ENTRANCE -> config.colorEntrance.getEffectiveColour()
			RoomType.FAIRY -> config.colorFairy.getEffectiveColour()
			RoomType.PUZZLE -> config.colorPuzzle.getEffectiveColour()
			RoomType.RARE -> config.colorRare.getEffectiveColour()
			RoomType.TRAP -> config.colorTrap.getEffectiveColour()
			else -> if (uniqueRoom?.hasMimic == true) config.colorRoomMimic.getEffectiveColour() else config.colorRoom.getEffectiveColour()
		}
}
