package net.wapic.wpcmod.util

import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting
import java.util.regex.Pattern

object ChatUtils {
	val formattingRegex: Pattern = Pattern.compile("(?i)§[0-9A-FK-OR]")
	val PREFIX: MutableComponent = Component.literal("[WpcMod]: ").setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA))

	fun sendMessage(message: String, style: Style = Style.EMPTY) {
		MC.inGameHud.chat.addMessage(PREFIX.copy().append(Component.literal(message).setStyle(style)))
	}

	fun sendAlert(title: MutableComponent, subtitle: MutableComponent = Component.literal(""), fadeInTicks: Int = 5, stayTicks: Int = 20, fadeOutTicks: Int = 5) = with(MC.inGameHud) {
		setTitle(title)
		setSubtitle(subtitle)
		setTimes(fadeInTicks, stayTicks, fadeOutTicks)
	}

	fun String.removeFormatting(): String = formattingRegex.matcher(this).replaceAll("")
}