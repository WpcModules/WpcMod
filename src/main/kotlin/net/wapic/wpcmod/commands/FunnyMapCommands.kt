package net.wapic.wpcmod.commands

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.wapic.wpcmod.features.funnymap.dungeon.Dungeon
import net.wapic.wpcmod.features.funnymap.dungeon.DungeonScan

object FunnyMapCommands : Command("dungeon") {

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		Dungeon.reset()
		DungeonScan.scan()
		return super.executes(context)
	}
}