package net.wapic.wpcmod.features.chat

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents
import net.wapic.wpcmod.WpcMod

object ChatEmotes {

	private val config get() = WpcMod.config.chat

	private val chatEmoteMap = mapOf(
		"o7" to "( ﾟ◡ﾟ)/",
		"<3" to "❤",
		":smile:" to "◕‿◕",
		":frown:" to "◉‸◉",
		":star:" to "✮",
		":yes:" to "✔",
		":no:" to "✖",
		":arrow:" to "➜",
		":flower:" to "✿",
		":typing:" to "✎...",
		":math:" to "√(π+x)=L",
		":shrug:" to "¯\\_(ツ)_/¯",
		":tableflip:" to "(╯°□°）╯︵ ┻━┻",
		":flip:" to "(╯°□°）╯︵ ┻━┻",
		":totem:" to "☉_☉",
		":gimme:" to "༼つ◕_◕༽つ",
		":wizard:" to "('-')⊃━☆ﾟ.*･｡ﾟ",
		":run:" to "ᕕ(՞ᗜ՞)ᕗ",
		":bear:" to "ʕ ᓀ ᴥ ᓂ ʔ",
		":rly:" to "ಠ_ಠ",
		":cry:" to "ಥ_ಥ",
		":sob:" to "ಥ_ಥ",
		":owo:" to "◉﹏◉",
		":dance:" to "♪ ┗(^o^)┓ ♪",
		":snail:" to "(0.o?)",
		":pvp:" to "⚔",
		":puffer:" to "<('O')>",
		":snow:" to "☃",
		":dog:" to "(ᵔᴥᵔ)",
		":sloth:" to "( ⬩ ⊝ ⬩ )",
		":dab:" to "<o/",
		":cat:" to "= ＾● ⋏ ●＾ =",
		":yey:" to "ヽ (◕◡◕) ﾉ",
		"h/" to "ヽ(^◇^*)/",
		":dj:" to "ヽ(⌐■_■)ノ♬",
		":cute:" to "(✿ᴖ‿ᴖ)",
		":skull:" to "☠",
		"o/" to "( ﾟ◡ﾟ)/",
	)

	val pattern = Regex("(?<=\\s|^)(" + chatEmoteMap.keys.joinToString("|") { Regex.escape(it) } + ")(?=\\s|$)")
	val commandPattern = Regex("^(m(sg|essage)?|w(hisper)?|r(eply)?|[pacg]c(hat)?)\\s.*$")

	fun init() {
		ClientSendMessageEvents.MODIFY_CHAT.register(::onSendMessage)
		ClientSendMessageEvents.MODIFY_COMMAND.register(::onSendCommand)
	}

	private fun onSendMessage(message: String): String {
		if (!config.chatEmotes) return message
		return replaceEmotes(message)
	}

	private fun onSendCommand(message: String): String {
		if (!config.chatEmotes || !commandPattern.matches(message)) return message
		return replaceEmotes(message)
	}

	private fun replaceEmotes(message: String): String = pattern.replace(message) { chatEmoteMap[it.value] ?: it.value }
}