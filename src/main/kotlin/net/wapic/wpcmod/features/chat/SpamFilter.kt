package net.wapic.wpcmod.features.chat

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.text.Text
import net.minecraft.util.math.MathHelper
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.chat.SpamConfig
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

	data class Notification(val text: Text, var delay: Int) {
		var x = MinecraftClient.getInstance().textRenderer.getWidth(text.string)
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

	fun onTick(client: MinecraftClient) {
		notifyQueue.toList().forEach { notification ->
			if (notification.delay == 0) notifyQueue.removeIf { it == notification }
			notification.delay--
		}
	}

	fun addToNotifyQueue(text: Text) {
		notifyQueue.add(Notification(text, 30))
	}

	fun handle(spamType: SpamConfig.SpamType, text: Text): Boolean {
		when (spamType) {
			SpamConfig.SpamType.SHOW -> return true
			SpamConfig.SpamType.HIDE -> return false

			SpamConfig.SpamType.NOTIFICATION -> {
				addToNotifyQueue(text)
				return false
			}
		}
	}

	fun onRenderHud(drawContext: DrawContext, tickCounter: RenderTickCounter) {
		val mc = MinecraftClient.getInstance()
		var y = drawContext.scaledWindowHeight - 48

		for (notification in notifyQueue.toList()) {
			val width = mc.textRenderer.getWidth(notification.text)
			val x1 = (drawContext.scaledWindowWidth - width) + notification.x
			drawContext.fill(x1, y - 2, x1 + width, y + 10, 0xaa121212.toInt())
			drawContext.drawText(mc.textRenderer, notification.text, x1, y, 0xffffff, false)
			y -= 12
			notification.x = MathHelper.lerp(tickCounter.dynamicDeltaTicks, notification.x, -12)
		}
	}

	fun onMessageReceived(text: Text, actionBar: Boolean): Boolean {
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