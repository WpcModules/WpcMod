package net.wapic.wpcmod.commands

import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.string
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.wapic.wpcmod.util.ChatUtils

object TagCommand : Command("tag") {

	val players = mutableSetOf<String>()

	private val commandClearPlayers = literal("clear").executes { clearPlayers() }

	private val playerArgument = argument("player", string()).executes { modifyTagList(it) }

	private fun modifyTagList(context: CommandContext<FabricClientCommandSource>): Int {
		val player = getString(context, "player").lowercase()
		if (players.contains(player)) {
			players.remove(player)
			ChatUtils.sendMessage("$player is no longer tagged")
		} else {
			players.add(player)
			ChatUtils.sendMessage("$player is now tagged")
		}
		return 0
	}

	private fun clearPlayers(): Int {
		players.clear()
		ChatUtils.sendMessage("Cleared Tag List")
		return 0
	}

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		ChatUtils.sendMessage("Tagged Players: ${players.joinToString { it }}")
		return super.executes(context)
	}

	override fun getCommand(): LiteralArgumentBuilder<FabricClientCommandSource> {
		return command.then(commandClearPlayers).then(playerArgument)
	}
}