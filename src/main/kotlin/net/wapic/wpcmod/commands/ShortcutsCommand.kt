package net.wapic.wpcmod.commands

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.Minecraft
import net.wapic.wpcmod.features.general.shortcut.ShortcutScreen
import net.wapic.wpcmod.util.MC

object ShortcutsCommand : Command("binds") {

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		context.source.client.schedule {
			val screen = MC.screen ?: return@schedule
			Minecraft.getInstance().setScreen(ShortcutScreen(screen))
		}
		return super.executes(context)
	}
}