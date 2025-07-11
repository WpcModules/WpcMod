package net.wapic.wpcmod.util

import net.minecraft.client.MinecraftClient
import net.minecraft.text.Style
import net.minecraft.text.Text

object ChatUtils {
    const val PREFIX = "§b[WpcMod]§r: "

    fun sendMessage(message: String, style: Style = Style.EMPTY){
        MinecraftClient.getInstance().inGameHud.chatHud.addMessage(
            Text.literal(PREFIX).append(Text.literal(message).setStyle(style))
        )
    }

    fun sendAlert(message: String, style: Style){
        MinecraftClient.getInstance().inGameHud.setTitle(Text.literal(message).setStyle(style))
        MinecraftClient.getInstance().inGameHud.setTitleTicks(0, 15, 5)
        MinecraftClient.getInstance().inGameHud.chatHud.addMessage(
            Text.literal(PREFIX).append(Text.literal(message).setStyle(style))
        )
    }
}