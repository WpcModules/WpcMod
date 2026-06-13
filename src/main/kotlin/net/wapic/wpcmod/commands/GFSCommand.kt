package net.wapic.wpcmod.commands

import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.string
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.SackUtils

object GFSCommand : Command("gfs") {

	private val skyBlockID = argument("skyblockId", string()).executes {
		SackUtils.getFromSack(getString(it, "skyblockId"), 64)
		return@executes 0
	}

	private val maxStackSize = argument("maxStackSize", integer(1, 64)).executes {
		SackUtils.getFromSack(getString(it, "skyblockId"), getInteger(it, "maxStackSize"))
		return@executes 0
	}

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		ChatUtils.sendMessage("Usage: /wpc gfs [SKYBLOCK ID] <Max Stack Size> (max stack size is assumed 64 unless provided)")
		return super.executes(context)
	}

	override fun getCommand(): LiteralArgumentBuilder<FabricClientCommandSource> {
		return command.then(skyBlockID.then(maxStackSize))
	}
}