package net.wapic.wpcmod.commands

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.ChatUtils

object UpdateCommand : Command("update") {

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		val updateFound = WpcMod.checkUpdate()
		if (updateFound) {
			WpcMod.sendUpdateMessage()
		} else {
			ChatUtils.sendMessage("No update available!")
		}
		return super.executes(context)
	}
}