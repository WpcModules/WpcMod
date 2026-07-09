package net.wapic.wpcmod.commands

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.wapic.wpcmod.features.general.shortcut.ShortcutScreen
import net.wapic.wpcmod.util.MC

object ShortcutsCommand : Command("binds") {

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		context.source.client.schedule {
			MC.screen = ShortcutScreen(null)
		}
		return super.executes(context)
	}
}