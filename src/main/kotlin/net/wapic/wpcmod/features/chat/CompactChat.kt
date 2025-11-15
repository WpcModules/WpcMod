package net.wapic.wpcmod.features.chat

import net.minecraft.text.*
import net.minecraft.util.Formatting
import net.minecraft.util.Util
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.mixin.accessors.ChatHudAccessor
import net.wapic.wpcmod.util.MC
import java.util.*

object CompactChat {

	private val config get() = WpcMod.config.chat

	data class CompactedMessage(
		var occurrences: Int = 1,
		var lastCompacted: Long = Util.getMeasuringTimeMs()
	) {
		fun setValues(reset: Boolean = false) {
			occurrences = if (reset) 1 else occurrences + 1
			lastCompacted = Util.getMeasuringTimeMs()
		}
	}

	private val messages = mutableMapOf<String, CompactedMessage>()
	private val separators = listOf("-----", "======", "▬▬▬▬▬▬")

	private fun shouldIgnore(message: String): Boolean {
		return !config.compactChat || message.isBlank() || separators.any(message::contains)
	}

	fun compactMessage(message: Text): Text {
		if (shouldIgnore(message.string)) return message

		val previousValue = messages.putIfAbsent(message.string, CompactedMessage())

		previousValue?.let { compactedMessage ->
			if (Util.getMeasuringTimeMs() - compactedMessage.lastCompacted >= config.compactTimeout * 1000) {
				compactedMessage.setValues(reset = true)
				return message
			}
			val chatHud = (MC.inGameHud.chatHud as ChatHudAccessor)

			compactedMessage.setValues()

			chatHud.messages.removeIf { oldMessage ->
				val contentWithoutOccurrences = oldMessage.content.copy()
				contentWithoutOccurrences.siblings.removeIf { it.content is OccurrenceTextContent }
				return@removeIf contentWithoutOccurrences.string == message.string
			}

			val occurrencesText = OccurrenceTextContent.create(compactedMessage.occurrences)
				.setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY))

			val scroll = chatHud.scrolledLines
			chatHud.wpcmod_refresh()
			MC.inGameHud.chatHud.scroll(scroll)

			return message.copy().append(occurrencesText)
		}

		return message
	}

	fun clear() {
		messages.clear()
	}

	class OccurrenceTextContent(var occurrences: Int) : PlainTextContent {

		override fun string(): String {
			return " (" + this.occurrences + ")"
		}

		override fun <T : Any> visit(visitor: StringVisitable.Visitor<T>): Optional<T> {
			return visitor.accept(this.string())
		}

		override fun <T : Any> visit(visitor: StringVisitable.StyledVisitor<T>, style: Style): Optional<T> {
			return visitor.accept(style, this.string())
		}

		override fun toString(): String {
			return "compactChatTextOccurrences{occurrences = " + this.occurrences + "}"
		}

		companion object {
			fun create(occurrences: Int): MutableText {
				return MutableText.of(OccurrenceTextContent(occurrences))
			}
		}
	}
}