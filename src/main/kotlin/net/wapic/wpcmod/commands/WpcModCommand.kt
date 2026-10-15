package net.wapic.wpcmod.commands

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.wapic.wpcmod.config.ConfigManager
import net.wapic.wpcmod.util.MC

object WpcModCommand : Command("wpc") {

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		MC.screen = ConfigManager.createScreen(null)
		return 1
	}
}