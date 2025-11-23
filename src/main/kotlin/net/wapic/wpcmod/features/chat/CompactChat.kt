package net.wapic.wpcmod.features.chat

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.SharedConstants
import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Util
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.mixin.accessors.ChatHudAccessor
import net.wapic.wpcmod.util.MC

object CompactChat {

	private val config get() = WpcMod.config.chat

	private val messages = mutableMapOf<Text, Message>()
	private var currentDividerSet: MutableList<Message>? = null
	private var compactingTicks = 0
	private const val PRUNE_TICK = 15 * SharedConstants.TICKS_PER_SECOND

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register { _ -> prune() }
		ClientReceiveMessageEvents.ALLOW_GAME.register { message, _ ->
			return@register if (!config.removeBlank) true else (Formatting.strip(message.string)?.isBlank() == false)
		}
	}

	private fun associateDividers(lastDivider: Message) {
		val divided = currentDividerSet
		check(divided != null)
		if (divided.size < 2) return
		divided[1].dividers.add(divided[0])
		divided.last().dividers.add(lastDivider)
	}

	private fun processDivider(message: Message) {
		currentDividerSet?.let {
			if (Util.getMeasuringTimeMs() > it.first().lastSeen + 5000) {
				WpcMod.logger.warn("Second divider wasn't received after 5 seconds!")
				currentDividerSet = null
			}
		}
		if (message.isDivider) {
			if (currentDividerSet == null) {
				currentDividerSet = mutableListOf()
			} else {
				associateDividers(message)
				currentDividerSet = null
			}
		}
		currentDividerSet?.add(message)
	}

	@JvmStatic
	fun compact(text: Text): Message? {
		if (!config.compactChat) return null

		var message: Message? = messages[text]?.takeUnless { it.isOld() }
		if (message == null) {
			message = Message(text.copy())
			if (!message.isDivider) messages[text] = message
		}
		processDivider(message)

		message.timesSeen++
		message.lastSeen = Util.getMeasuringTimeMs()

		if (message.shouldCompact) message.remove()
		return message
	}

	@JvmStatic
	fun clear() {
		messages.clear()
		currentDividerSet = null
	}

	@JvmStatic
	fun prune() {
		if (compactingTicks++ % PRUNE_TICK == 0 && config.compactChat) {
			messages.values.removeIf(Message::isOld)
		}
	}

	@JvmStatic
	fun buildLineCache(): Map<ChatHudLine, Message> =
		messages.entries.mapNotNull { (it.value.lastLine ?: return@mapNotNull null) to it.value }.toMap()

	class Message(val text: MutableText) {
		var lastLine: ChatHudLine? = null
		val lastVisible: MutableList<ChatHudLine.Visible> = mutableListOf()
		val dividers: MutableList<Message> = mutableListOf()

		var timesSeen: Int = 0
		var lastSeen: Long = Util.getMeasuringTimeMs()

		val isDivider: Boolean by lazy {
			val message = Formatting.strip(text.string)!!
			message.length > 5 && message.all { it == '-' || it == '=' || it == '\u25AC' }
		}

		val textWithCounter: Text
			get() = if (timesSeen == 1) text else {
				text.copy().append(
					Text.literal(" ($timesSeen)").setStyle(Style.EMPTY.withExclusiveFormatting(Formatting.DARK_GRAY))
				)
		}

		@get:JvmName("shouldCompact")
		val shouldCompact: Boolean get() = timesSeen > 1 && !isDivider

		fun isOld(): Boolean = Util.getMeasuringTimeMs() >= lastSeen + config.compactTimeout * 1000

		fun remove() {
			val hud = MC.inGameHud.chatHud as ChatHudAccessor
			lastLine?.let(hud.messages::remove)
			lastVisible.forEach(hud.visibleMessages::remove)
			lastVisible.clear()
			dividers.forEach(Message::remove)
			dividers.clear()
		}
	}
}