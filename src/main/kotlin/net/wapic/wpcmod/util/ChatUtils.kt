package net.wapic.wpcmod.util

import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.regex.Pattern

object ChatUtils {
	val formattingRegex: Pattern = Pattern.compile("(?i)§[0-9A-FK-OR]")
	val PREFIX: MutableText = Text.literal("[WpcMod]: ").setStyle(Style.EMPTY.withColor(Formatting.AQUA))

	fun sendMessage(message: String, style: Style = Style.EMPTY) {
		MC.inGameHud.chatHud.addMessage(PREFIX.copy().append(Text.literal(message).setStyle(style)))
	}

	fun sendAlert(title: MutableText, subtitle: MutableText = Text.literal(""), fadeInTicks: Int = 5, stayTicks: Int = 20, fadeOutTicks: Int = 5) = with(MC.inGameHud) {
		setTitle(title)
		setSubtitle(subtitle)
		setTitleTicks(fadeInTicks, stayTicks, fadeOutTicks)
		chatHud.addMessage(PREFIX.copy().append(title).append(subtitle))
	}

	fun sendServerMessage(message: String) = with(MC.networkHandler) {
		this?.sendChatMessage(message)
	}

	fun String.removeFormatting(): String = formattingRegex.matcher(this).replaceAll("");
}