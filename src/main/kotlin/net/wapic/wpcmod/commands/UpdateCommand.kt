package net.wapic.wpcmod.commands

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.ChatUtils

object UpdateCommand : Command("update") {

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		val potentialUpdate = WpcMod.getPotentialUpdate()

		if (potentialUpdate.isUpdateAvailable) {
			WpcMod.sendUpdateMessage(potentialUpdate)
		} else {
			ChatUtils.sendMessage("No Updates Available")
		}

		return super.executes(context)
	}
}