package net.wapic.wpcmod.commands

import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.string
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.network.chat.Component
import net.wapic.wpcmod.util.ChatUtils

object SimulateCommand : Command("simulate") {

	private val message = argument("message", string()).executes {
		ClientReceiveMessageEvents.GAME.invoker().onReceiveGameMessage(Component.literal(getString(it, "message")), false)
		return@executes 1
	}

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		ChatUtils.sendMessage("Usage: /wpc simulate <message>")
		return 1
	}

	override fun getCommand(): LiteralArgumentBuilder<FabricClientCommandSource> {
		return command.then(message)
	}
}