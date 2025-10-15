package net.wapic.wpcmod.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.wapic.wpcmod.features.dungeons.floor7.termsim.MelodySim
import net.wapic.wpcmod.features.dungeons.floor7.termsim.NumbersSim
import net.wapic.wpcmod.features.dungeons.floor7.termsim.PanesSim
import net.wapic.wpcmod.features.dungeons.floor7.termsim.RubixSim
import net.wapic.wpcmod.features.dungeons.floor7.termsim.SelectAllSim
import net.wapic.wpcmod.features.dungeons.floor7.termsim.StartGUI
import net.wapic.wpcmod.features.dungeons.floor7.termsim.StartsWithSim

object TermSimCommand : Command("term") {

	private val startsWith = literal("startswith").executes {
		StartsWithSim().open()
		return@executes 0
	}

	private val panes = literal("panes").executes {
		PanesSim.open()
		return@executes 0
	}
	private val selectAll = literal("selectall").executes {
		SelectAllSim().open()
		return@executes 0
	}
	private val numbers = literal("numbers").executes {
		NumbersSim.open()
		return@executes 0
	}
	private val melody = literal("melody").executes {
		MelodySim.open()
		return@executes 0
	}
	private val rubix = literal("rubix").executes {
		RubixSim.open()
		return@executes 0
	}

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		StartGUI.open()
		return super.executes(context)
	}

	override fun getCommand(): LiteralArgumentBuilder<FabricClientCommandSource> {
		return command.then(rubix).then(melody).then(selectAll).then(panes).then(numbers).then(startsWith)
	}
}