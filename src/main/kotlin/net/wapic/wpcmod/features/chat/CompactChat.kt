package net.wapic.wpcmod.features.chat

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.ChatFormatting
import net.minecraft.SharedConstants
import net.minecraft.client.multiplayer.chat.GuiMessage
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.util.Util
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.mixin.accessors.ChatComponentAccessor
import net.wapic.wpcmod.util.MC

object CompactChat {

	private val config get() = WpcMod.config.chat.compactChat

	private val messages = mutableMapOf<Component, Message>()
	private var currentDividerSet: MutableList<Message>? = null
	private var compactingTicks = 0
	private const val PRUNE_TICK = 15 * SharedConstants.TICKS_PER_SECOND

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register { _ -> prune() }
		ClientReceiveMessageEvents.ALLOW_GAME.register { message, _ ->
			return@register if (!config.removeBlank) true else ChatFormatting.stripFormatting(message.string)
				?.isBlank() == false
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
			if (Util.getMillis() > it.first().lastSeen + 5000) {
				WpcMod.LOGGER.warn("Second divider wasn't received after 5 seconds!")
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
	fun compact(text: Component): Message? {
		if (!config.enabled) return null

		var message: Message? = messages[text]?.takeUnless { it.isOld() }
		if (message == null) {
			message = Message(text.copy())
			if (!message.isDivider) messages[text] = message
		}
		processDivider(message)

		message.timesSeen++
		message.lastSeen = Util.getMillis()

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
		if (compactingTicks++ % PRUNE_TICK == 0 && config.enabled) {
			messages.values.removeIf(Message::isOld)
		}
	}

	@JvmStatic
	fun buildLineCache(): Map<GuiMessage, Message> =
		messages.entries.mapNotNull { (it.value.lastLine ?: return@mapNotNull null) to it.value }.toMap()

	class Message(val text: MutableComponent) {
		var lastLine: GuiMessage? = null
		val lastVisible: MutableList<GuiMessage.Line> = mutableListOf()
		val dividers: MutableList<Message> = mutableListOf()

		var timesSeen: Int = 0
		var lastSeen: Long = Util.getMillis()

		val isDivider: Boolean by lazy {
			val message = ChatFormatting.stripFormatting(text.string)!!
			message.length > 5 && message.all { it == '-' || it == '=' || it == '\u25AC' }
		}

		val textWithCounter: Component
			get() = if (timesSeen == 1) text else {
				text.copy().append(
					Component.literal(" ($timesSeen)").setStyle(Style.EMPTY.applyLegacyFormat(ChatFormatting.DARK_GRAY))
				)
		}

		@get:JvmName("shouldCompact")
		val shouldCompact: Boolean get() = timesSeen > 1 && !isDivider

		fun isOld(): Boolean = Util.getMillis() >= lastSeen + config.compactTimeout * 1000

		fun remove() {
			val hud = MC.gui.chat as ChatComponentAccessor
			lastLine?.let(hud.allMessages::remove)
			lastVisible.forEach(hud.trimmedMessages::remove)
			lastVisible.clear()
			dividers.forEach(Message::remove)
			dividers.clear()
		}
	}
}