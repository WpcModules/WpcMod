package net.wapic.wpcmod.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.MinecraftClient
import net.wapic.wpcmod.features.general.shortcut.ShortcutScreen
import net.wapic.wpcmod.features.inventory.inventorybinds.InventoryBindsEditor
import net.wapic.wpcmod.util.MC

object ShortcutsCommand : Command("binds") {

	private val inventory = literal("inv").executes {
		MC.instance.send { MC.screen = InventoryBindsEditor() }
		return@executes 0
	}

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		context.source.client.send {
			MinecraftClient.getInstance().setScreen(ShortcutScreen())
		}
		return super.executes(context)
	}

	override fun getCommand(): LiteralArgumentBuilder<FabricClientCommandSource> {
		return this.command.then(this.inventory)
	}
}