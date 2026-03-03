package net.wapic.wpcmod.features.chat

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
import net.minecraft.util.Mth
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.chat.SpamConfig
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils

object SpamFilter {

	private val config get() = WpcMod.config.chat.spam

	private val notifyQueue = mutableListOf<Notification>()

	private val abilityRegex =
		Regex("^Your (\\w+(?:\\s\\w+)*) hit (\\d+) enem(?:y|ies) for (\\d+(?:,\\d+)*(\\.\\d+)?) damage\\.$")
	private val tpFailRegex = Regex("^There are blocks in the way!$")
	private val killComboRegex =
		Regex("\\+\\d+ Kill Combo(?: \\+\\d+[%☯]? (?:✯ Magic Find|coins per kill|Combat Wisdom))?")
	private val joinOrLeaveRegex = Regex("^(?:Friend|Guild) > \\w+ (?:joined|left)\\.$")

	data class Notification(val text: Component, var delay: Int) {
		var x = MC.font.width(text.string)
	}

	fun init() {
		ClientReceiveMessageEvents.ALLOW_GAME.register(::onMessageReceived)
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
		HudElementRegistry.attachElementBefore(
			VanillaHudElements.DEMO_TIMER,
			Utils.modIdentifier("spam_filter"),
			::onRenderHud
		)
	}

	fun onTick(client: Minecraft) {
		if (notifyQueue.isEmpty()) return
		notifyQueue.toList().forEach { notification ->
			if (notification.delay == 0) notifyQueue.removeIf { it == notification }
			notification.delay--
		}
	}

	fun addToNotifyQueue(text: Component) {
		notifyQueue.add(Notification(text, 30))
	}

	fun handle(spamType: SpamConfig.SpamType, text: Component): Boolean {
		when (spamType) {
			SpamConfig.SpamType.SHOW -> return true
			SpamConfig.SpamType.HIDE -> return false

			SpamConfig.SpamType.NOTIFICATION -> {
				addToNotifyQueue(text)
				return false
			}
		}
	}

	fun onRenderHud(drawContext: GuiGraphics, tickCounter: DeltaTracker) {
		var y = drawContext.guiHeight() - 48

		for (notification in notifyQueue.toList()) {
			val width = MC.font.width(notification.text)
			val x1 = (drawContext.guiWidth() - width) + notification.x
			drawContext.fill(x1, y - 2, x1 + width, y + 10, 0xaa121212.toInt())
			drawContext.drawString(MC.font, notification.text, x1, y, CommonColors.WHITE, false)
			y -= 12
			notification.x = Mth.lerpInt(tickCounter.gameTimeDeltaTicks, notification.x, -12)
		}
	}

	fun onMessageReceived(text: Component, actionBar: Boolean): Boolean {
		if (!actionBar) {
			if (text.string.matches(abilityRegex)) return handle(config.abilityHit, text)
			if (text.string.matches(tpFailRegex)) return handle(config.tpFail, text)
			if (text.string.matches(killComboRegex)) return handle(config.killCombo, text)
			if (text.string.matches(joinOrLeaveRegex)) return handle(config.joinOrLeave, text)
			return true
		}
		return true
	}
}