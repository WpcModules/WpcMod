package net.wapic.wpcmod.features.chat

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents

object ChatEmotes {
	private val chatEmoteMap = mapOf(
		"o7" to "( ﾟ◡ﾟ)/",
		"<3" to "❤",
		":)" to "◕‿◕",
		":(" to "◉‸◉",
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

	fun init() {
		ClientSendMessageEvents.MODIFY_CHAT.register(::replaceText)
		ClientSendMessageEvents.MODIFY_COMMAND.register(::replaceText)
	}

	fun replaceText(message: String): String {
		val pattern = Regex("(?<=\\s|^)(" + chatEmoteMap.keys.joinToString("|") { Regex.escape(it) } + ")(?=\\s|$)")

		val result = pattern.replace(message) { match ->
			chatEmoteMap[match.value] ?: match.value
		}
		return result
	}
}