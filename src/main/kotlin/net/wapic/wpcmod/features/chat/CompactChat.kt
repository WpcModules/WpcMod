package net.wapic.wpcmod.features.chat

import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.text.*
import net.minecraft.util.Formatting
import net.minecraft.util.Util
import net.wapic.wpcmod.WpcMod
import java.util.*

object CompactChat {

	private val config get() = WpcMod.config.chatConfig

	data class CompactedMessage(
		val text: MutableText,
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

	fun compactMessage(message: Text, chatHudLines: MutableList<ChatHudLine>): Text {
		if (shouldIgnore(message.string)) return message

		val previousValue = messages.putIfAbsent(message.string, CompactedMessage(message.copy()))

		previousValue?.let { compactedMessage ->
			if (Util.getMeasuringTimeMs() - compactedMessage.lastCompacted >= config.compactTimeout * 1000) {
				compactedMessage.setValues(reset = true)
				return message
			}

			compactedMessage.setValues()

			val iterator = chatHudLines.iterator()
			while (iterator.hasNext()) {
				val chatLine = iterator.next()

				val contentWithoutOccurrences = chatLine.content.copy()
				contentWithoutOccurrences.siblings.removeIf { it.content is OccurrenceTextContent }

				if (contentWithoutOccurrences.string == message.string) {
					iterator.remove()
				}
			}

			val occurrencesText = OccurrenceTextContent.create(compactedMessage.occurrences)
				.setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY))
			return message.copy().append(occurrencesText)
		}

		return message
	}

	fun clear() {
		messages.clear()
	}

	class OccurrenceTextContent(var occurrences: Int) : PlainTextContent {

		override fun string(): String? {
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