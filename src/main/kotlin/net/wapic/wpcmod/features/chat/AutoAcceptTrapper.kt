package net.wapic.wpcmod.features.chat

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils

object AutoAcceptTrapper {

	private val config = WpcMod.config.chat

	fun init() {
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
	}

	fun onMessageReceived(message: Component, actionBar: Boolean) {
		if (actionBar || Utils.getLocation() != Island.BARN || !config.autoAcceptTrapper) return

		if (message.string.equals("\nAccept the trapper's task to hunt the animal?\nClick an option: §a§l[YES] - §c§l[NO]")) {
			val yesComponent = message.siblings.first { it.string == "§a§l[YES]" }
			val clickEvent = yesComponent.style.clickEvent as? ClickEvent.RunCommand
			Utils.runCommand(clickEvent?.command ?: return ChatUtils.sendMessage("Unable to find click event"))
		}
	}
}