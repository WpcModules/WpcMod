package net.wapic.wpcmod.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.wapic.wpcmod.hud.HudManager

object HudEditorCommand : Command("hud") {

	private val resetHud = literal("reset").executes {
		HudManager.resetLocations()
		return@executes 0
	}

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		HudManager.openEditor()
		return super.executes(context)
	}

	override fun getCommand(): LiteralArgumentBuilder<FabricClientCommandSource> {
		return this.command.then(resetHud)
	}
}