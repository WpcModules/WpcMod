package net.wapic.wpcmod.commands

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.MinecraftClient
import net.wapic.wpcmod.features.general.shortcut.ShortcutScreen

object ShortcutsCommand : Command("binds") {

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		context.source.client.send {
			MinecraftClient.getInstance().setScreen(ShortcutScreen())
		}
		return super.executes(context)
	}
}