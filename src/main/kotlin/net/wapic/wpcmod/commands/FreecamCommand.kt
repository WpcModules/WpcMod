package net.wapic.wpcmod.commands

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.wapic.wpcmod.features.general.Freecam

object FreecamCommand : Command("freecam") {

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		Freecam.toggle(context.source.client)
		return 1
	}

}