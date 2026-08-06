package net.wapic.wpcmod.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.dungeons.floor7.terminals.TerminalSimulator
import net.wapic.wpcmod.features.dungeons.floor7.terminals.Terminal

object TermSimCommand : Command("term") {
	private val ping get() = WpcMod.config.dungeon.floor7.termSimPing.toLong()

	private val startsWith = literal("startswith").executes {
		TerminalSimulator.open(Terminal.Type.STARTS_WITH, null, null)
		return@executes 0
	}

	private val panes = literal("panes").executes {
		TerminalSimulator.open(Terminal.Type.PANES)
		return@executes 0
	}

	private val selectAll = literal("selectall").executes {
		TerminalSimulator.open(Terminal.Type.SELECT_ALL)
		return@executes 0
	}

	private val numbers = literal("numbers").executes {
		TerminalSimulator.open(Terminal.Type.NUMBERS)
		return@executes 0
	}

	private val melody = literal("melody").executes {
		TerminalSimulator.open(Terminal.Type.MELODY)
		return@executes 0
	}

	private val rubix = literal("rubix").executes {
		TerminalSimulator.open(Terminal.Type.RUBIX)
		return@executes 0
	}

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		return super.executes(context)
	}

	override fun getCommand(): LiteralArgumentBuilder<FabricClientCommandSource> {
		return command.then(rubix).then(melody).then(selectAll).then(panes).then(numbers).then(startsWith)
	}
}