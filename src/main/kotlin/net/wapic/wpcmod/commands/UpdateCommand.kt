package net.wapic.wpcmod.commands

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.ChatUtils

object UpdateCommand : Command("update") {

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		val future = WpcMod.updateContext.checkUpdate("upstream")
		val potentialUpdate = future.get()

		if (potentialUpdate.isUpdateAvailable) {
			ChatUtils.sendMessage("Launching update...")
			potentialUpdate.launchUpdate().thenRun {
				ChatUtils.sendMessage("Download complete! Update will apply after you restart")
			}
		} else {
			ChatUtils.sendMessage("No Updates Available")
		}

		return super.executes(context)
	}
}