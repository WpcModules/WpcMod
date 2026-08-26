package net.wapic.wpcmod.features.hunting

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.SafariAPI
import net.wapic.wpcmod.util.Utils

object AutoAcceptHideyho {

	private val config = WpcMod.config.hunting.safari

	// Hypixel, trim your damn messages please
	private const val HIDEYHO_ACCEPT_MESSAGE = "§eSelect an option: §a[Sure] §c[No thanks...] "

	fun init() {
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
	}

	fun onMessageReceived(message: Component, actionBar: Boolean) {
		if (actionBar || !SafariAPI.inSafari || !config.autoAcceptHideyho) return

		if (message.string == HIDEYHO_ACCEPT_MESSAGE) {
			val yesComponent = message.siblings.first { it.string == "§a[Sure] " }
			val clickEvent = yesComponent.style.clickEvent as? ClickEvent.RunCommand
			Utils.runCommand(clickEvent?.command ?: return ChatUtils.sendMessage("Unable to find click event"))
		}
	}
}