package net.wapic.wpcmod.commands

import com.mojang.brigadier.context.CommandContext
import io.github.notenoughupdates.moulconfig.common.IMinecraft
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.wapic.wpcmod.config.ConfigManager

object WpcModCommand : Command("wpcmod") {

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		context.source.client.send {
			IMinecraft.instance.openWrappedScreen(ConfigManager.getEditor())
		}
		return super.executes(context)
	}
}