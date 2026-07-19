package net.wapic.wpcmod.commands

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.wapic.wpcmod.WpcMod

object UpdateCommand : Command("update") {

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		WpcMod.checkUpdate()
		return 1
	}
}