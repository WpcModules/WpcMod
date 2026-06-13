package net.wapic.wpcmod.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

open class Command {

	@JvmField
	var command: LiteralArgumentBuilder<FabricClientCommandSource>

	constructor(commandName: String) {
		this.command = literal(commandName).executes { context ->
			executes(context)
		}
	}

	open fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		return 0
	}

	open fun getCommand(): LiteralArgumentBuilder<FabricClientCommandSource> {
		return this.command
	}
}