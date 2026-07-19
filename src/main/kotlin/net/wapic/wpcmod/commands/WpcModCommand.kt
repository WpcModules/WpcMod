package net.wapic.wpcmod.commands

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.wapic.wpcmod.config.ConfigManager.openConfig

object WpcModCommand : Command("wpc") {

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		openConfig(context.source.client)
		return 1
	}
}