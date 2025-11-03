package net.wapic.wpcmod.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Style
import net.minecraft.util.Formatting
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.DungeonScan
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.FunnyMap
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.ScanUtils
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils

object FunnyMapCommands : Command("dungeon") {

	val getRoomData: LiteralArgumentBuilder<FabricClientCommandSource> = literal("room").executes{
		val player = MC.player?.entityPos ?: return@executes 0
		val pos = ScanUtils.getRoomCentre(player.x.toInt(), player.z.toInt())
		val data = ScanUtils.getRoomData(pos.first, pos.second)
		if (data != null) {
			Utils.copyToClipboard(data.toString())
			ChatUtils.sendMessage("Copied room data to clipboard.", Style.EMPTY.withColor(Formatting.GREEN))
		} else {
			Utils.copyToClipboard("${ScanUtils.getCore(pos.first, pos.second)}")
			ChatUtils.sendMessage("§cExisting room data not found. §aCopied room core to clipboard.")
		}
	0
	}

	val scan: LiteralArgumentBuilder<FabricClientCommandSource> = literal("scan").executes {
		FunnyMap.reset()
		DungeonScan.scan()
		0
	}

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		ChatUtils.sendMessage("Usage: /wpc dungeon <scan/room>", Style.EMPTY.withColor(Formatting.RED))
		return super.executes(context)
	}

	override fun getCommand(): LiteralArgumentBuilder<FabricClientCommandSource> {
		return command.then(getRoomData).then(scan)
	}
}