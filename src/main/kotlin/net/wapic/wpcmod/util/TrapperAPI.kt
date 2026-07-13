package net.wapic.wpcmod.util

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.network.chat.Component
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.ChatUtils.removeFormatting

object TrapperAPI {

	private val TRAPPER_QUEST_MESSAGE =
		Regex("^\\[NPC] Trevor: You can find your (?<mobType>(?:UN)?(?:TRACKABLE|DETECTED)|E(?:LUSIVE|NDANGERED)) animal near the (?<area>.*)\\.$")

	var currentType: TrapperType? = null
		private set
	var currentArea: String? = null

	enum class TrapperType(val maxHealth: Float) {
		TRACKABLE(100F),
		UNTRACKABLE(500F),
		UNDETECTED(1000F),
		ENDANGERED(5000F),
		ELUSIVE(10000F);
	}

	fun init() {
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
	}

	fun onMessageReceived(message: Component, isActionBar: Boolean) {
		if (isActionBar) return

		val matcher = TRAPPER_QUEST_MESSAGE.matchEntire(message.string.removeFormatting()) ?: return
		val mobType =
			matcher.groups["mobType"]?.value ?: return WpcMod.LOGGER.error("Unable to find mob type from Trapper Quest")
		currentType = TrapperType.valueOf(mobType)
		currentArea =
			matcher.groups["area"]?.value ?: return WpcMod.LOGGER.error("Unable to find area from Trapper Quest")
		WpcMod.LOGGER.debug("Trapper Quest: {} in {}", currentType, currentArea)
	}
}