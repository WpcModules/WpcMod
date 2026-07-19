package net.wapic.wpcmod.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.dungeons.floor7.TerminalSimulator
import net.wapic.wpcmod.features.dungeons.floor7.termsim.*

object TermSimCommand : Command("term") {
	private val ping get() = WpcMod.config.dungeon.floor7.termSimPing.toLong()

	private val startsWith = literal("startswith").executes {
		StartsWithSim().open(ping)
		return@executes 1
	}

	private val panes = literal("panes").executes {
		PanesSim.open(ping)
		return@executes 1
	}

	private val selectAll = literal("selectall").executes {
		SelectAllSim().open(ping)
		return@executes 1
	}

	private val numbers = literal("numbers").executes {
		NumbersSim.open(ping)
		return@executes 1
	}

	private val melody = literal("melody").executes {
		MelodySim.open(ping)
		return@executes 1
	}

	private val rubix = literal("rubix").executes {
		RubixSim.open(ping)
		return@executes 1
	}

	private val random = literal("random").executes {
		TerminalSimulator.openRandomTerminal(ping)
		return@executes 1
	}

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		StartGUI.open(ping)
		return 1
	}

	override fun getCommand(): LiteralArgumentBuilder<FabricClientCommandSource> {
		return command.then(rubix).then(melody).then(selectAll).then(panes).then(numbers).then(startsWith).then(random)
	}
}