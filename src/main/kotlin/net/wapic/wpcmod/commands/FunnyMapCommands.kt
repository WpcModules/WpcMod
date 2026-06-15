package net.wapic.wpcmod.commands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Style
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.DungeonScan
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.FunnyMap
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.ScanUtils
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils

object FunnyMapCommands : Command("dungeon") {

	val roomDataFromPlayer: LiteralArgumentBuilder<FabricClientCommandSource> = literal("room").executes {
		val pos = MC.cameraPos ?: return@executes 0

		val roomCentre = ScanUtils.getRoomCentre(pos.x.toInt(), pos.z.toInt())
		val data = ScanUtils.getRoomData(roomCentre.first, roomCentre.second)

		if (data != null) {
			Utils.copyToClipboard(data.toString())
			ChatUtils.sendMessage("Copied room data to clipboard.", Style.EMPTY.withColor(ChatFormatting.GREEN))
		} else {
			Utils.copyToClipboard("${ScanUtils.getCore(roomCentre.first, roomCentre.second)}")
			ChatUtils.sendMessage("§cExisting room data not found. §aCopied room core to clipboard.")
		}
		return@executes 0
	}

	val addCore: LiteralArgumentBuilder<FabricClientCommandSource> = literal("addcore").executes {
		ChatUtils.sendMessage("Usage: /wpc dungeon room addcore <roomName>", Style.EMPTY.withColor(ChatFormatting.RED))
		return@executes 0
	}

	val addCoreFromRoomName: RequiredArgumentBuilder<FabricClientCommandSource, String> =
		argument("roomName", StringArgumentType.string()).executes {
			if (Utils.getLocation() != Island.DUNGEON) {
				ChatUtils.sendMessage(
					"You must be in a dungeon to add a core.",
					Style.EMPTY.withColor(ChatFormatting.RED)
				)
				return@executes 1
			}
			val roomName = StringArgumentType.getString(it, "roomName")
			val data = ScanUtils.addCore(roomName)
			return@executes 0
		}

	val saveRooms: LiteralArgumentBuilder<FabricClientCommandSource> = literal("save").executes {
		ScanUtils.saveRoomList()
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
		return command.then(scan).then(roomDataFromPlayer.then(saveRooms).then(addCore.then(addCoreFromRoomName)))
	}
}