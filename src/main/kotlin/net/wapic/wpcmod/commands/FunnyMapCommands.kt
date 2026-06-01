package net.wapic.wpcmod.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Style
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.DungeonScan
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.FunnyMap
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.ScanUtils
import net.wapic.wpcmod.features.general.Freecam
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils

object FunnyMapCommands : Command("dungeon") {

	val roomDataFromPlayer: LiteralArgumentBuilder<FabricClientCommandSource> = literal("room").executes {
		val pos = if(Freecam.isEnabled) {
			MC.instance.cameraEntity?.position() ?: return@executes 0
		} else {
			MC.player?.position() ?: return@executes 0
		}

		val roomCentre = ScanUtils.getRoomCentre(pos.x.toInt(), pos.z.toInt())
		val currentCore = ScanUtils.getCore(roomCentre.first, roomCentre.second)
		val data = ScanUtils.getRoomData(currentCore)

		if (data != null) {
			Utils.copyToClipboard("$data")
			ChatUtils.sendMessage(
				"Copied room data to clipboard from core: $currentCore",
				Style.EMPTY.withColor(ChatFormatting.GREEN)
			)
		} else {
			Utils.copyToClipboard("$currentCore")
			ChatUtils.sendMessage("§cExisting room data not found. §aCopied room core to clipboard.")
		}
		return@executes 0
	}

	val scan: LiteralArgumentBuilder<FabricClientCommandSource> = literal("scan").executes {
		FunnyMap.reset()
		DungeonScan.scan()
		return@executes 0
	}

	override fun executes(context: CommandContext<FabricClientCommandSource>): Int {
		ChatUtils.sendMessage("Usage: /wpc dungeon <scan/room>", Style.EMPTY.withColor(ChatFormatting.RED))
		return super.executes(context)
	}

	override fun getCommand(): LiteralArgumentBuilder<FabricClientCommandSource> {
		return command.then(roomDataFromPlayer).then(scan)
	}
}