package net.wapic.wpcmod.features.funnymap.utils

import net.minecraft.util.Identifier
import net.wapic.wpcmod.features.funnymap.core.map.RoomState
import net.wapic.wpcmod.features.funnymap.dungeon.MapRender
import net.wapic.wpcmod.util.MC

class CheckmarkSet(val size: Int, location: String) {
	private val crossResource = Identifier.of("wpcmod", "$location/cross.png")
	private val greenResource = Identifier.of("wpcmod", "$location/green_check.png")
	private val questionResource = Identifier.of("wpcmod", "$location/question.png")
	private val whiteResource = Identifier.of("wpcmod", "$location/white_check.png")

	init {
		listOf(crossResource, greenResource, questionResource, whiteResource).forEach {
			MC.textureManager.registerTexture(it)
		}
	}

	fun getCheckmark(state: RoomState): Identifier? {
		return when (state) {
			RoomState.CLEARED -> whiteResource
			RoomState.GREEN -> greenResource
			RoomState.FAILED -> crossResource
			RoomState.UNOPENED -> if (MapRender.legitRender) questionResource else null
			else -> null
		}
	}
}
