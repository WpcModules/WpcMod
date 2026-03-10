package net.wapic.wpcmod.commands

import com.mojang.brigadier.arguments.StringArgumentType.string
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.wapic.wpcmod.features.entity.TagESP
import net.wapic.wpcmod.util.ChatUtils

object TagCommand : Command("tag") {

	private val entityArgument = argument("entityName", string()).executes {
		TagESP.modifyTagList(it)
		return@executes 1
	}

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		ChatUtils.sendMessage("Tagged entities: ${TagESP.getTagList()}")
		return super.executes(context)
	}

	override fun getCommand(): LiteralArgumentBuilder<FabricClientCommandSource> {
		return command.then(entityArgument)
	}
}