package net.wapic.wpcmod.features.dungeons.funnymap.core.map

import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.dungeons.funnymap.core.RoomData
import net.wapic.wpcmod.features.dungeons.funnymap.ui.MapElement
import java.awt.Color

class Room(override val x: Int, override val z: Int, var data: RoomData) : Tile {

	private val config get() = WpcMod.config.dungeon.funnyMap
	private val colors get() = WpcMod.config.dungeon.funnyMap.colors

	var core = 0
	var isSeparator = false
	var uniqueRoom: UniqueRoom? = null

	override var state: RoomState = RoomState.UNDISCOVERED
	override val color: Color
		get() = if (MapElement.legitRender && state == RoomState.UNOPENED) colors.colorUnopened.getEffectiveColour()
		else when (data.type) {
			RoomType.BLOOD -> colors.colorBlood.getEffectiveColour()
			RoomType.CHAMPION -> colors.colorMiniboss.getEffectiveColour()
			RoomType.ENTRANCE -> colors.colorEntrance.getEffectiveColour()
			RoomType.FAIRY -> colors.colorFairy.getEffectiveColour()
			RoomType.PUZZLE -> colors.colorPuzzle.getEffectiveColour()
			RoomType.RARE -> colors.colorRare.getEffectiveColour()
			RoomType.TRAP -> colors.colorTrap.getEffectiveColour()
			else -> if (uniqueRoom?.hasMimic == true && !config.legitMode) colors.colorRoomMimic.getEffectiveColour() else colors.colorRoom.getEffectiveColour()
		}
}
