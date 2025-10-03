package net.wapic.wpcmod.commands

import com.mojang.brigadier.arguments.StringArgumentType.string
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.wapic.wpcmod.features.entity.TagESP
import net.wapic.wpcmod.util.ChatUtils

object TagCommand : Command("tag") {

	private val commandClearPlayers = literal("clear").executes {
		TagESP.taggedEntities.clear()
		return@executes 0
	}

	private val playerArgument = argument("player", string()).executes {
		TagESP.modifyTagList(it)
		return@executes 0
	}

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		ChatUtils.sendMessage("Tagged Players: ${TagESP.taggedEntities.joinToString { it }}")
		return super.executes(context)
	}

	override fun getCommand(): LiteralArgumentBuilder<FabricClientCommandSource> {
		return command.then(commandClearPlayers).then(playerArgument)
	}
}