package net.wapic.wpcmod.features.chat

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.Style
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Calculator
import net.wapic.wpcmod.util.Utils

object QuickMathSolver {

	private val config get() = WpcMod.config.chat.quickMaths
	private val quickMathsRegex = Regex("^QUICK MATHS! Solve: (?<equation>.*)$")

	fun init() {
		ClientReceiveMessageEvents.MODIFY_GAME.register(::onMessageReceived)
	}

	fun onMessageReceived(message: Component, isActionBar: Boolean): Component {
		if (isActionBar || !config.enabled) return message

		val mathMatch = quickMathsRegex.matchEntire(message.string) ?: return message
		val equation = mathMatch.groups["equation"]?.value ?: return message
		WpcMod.LOGGER.debug("Got equation: $equation")

		Calculator(equation).eval()?.let {
			WpcMod.LOGGER.debug("Equation result: {}", it)

			if (config.autoAnswer) {
				Utils.addToCommandQueue("ac $it")
				return message
			}

			val originalMessage = message.copy()
			val result = Component.literal("§7 = §e$it§r")
			val clickEvent = ClickEvent.RunCommand("ac $it")
			val hoverText = HoverEvent.ShowText(Component.literal("Click to Answer!"))
			val style = Style.EMPTY.withHoverEvent(hoverText).withClickEvent(clickEvent)

			return originalMessage.append(result).withStyle(style)
		}

		return message
	}
}