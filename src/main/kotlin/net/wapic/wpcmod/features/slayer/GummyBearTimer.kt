package net.wapic.wpcmod.features.slayer

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.hud.SimpleHudElement
import net.wapic.wpcmod.util.MC

object GummyBearTimer : SimpleHudElement("Gummy Bear Timer", 110, 11) {

	private val config get() = WpcMod.config.slayer
	override val isEnabled: Boolean get() = config.gummyBearTimer.enable

	private const val GUMMY_BEAR_LENGTH = 3_600_000
	private const val GUMMY_BEAR_MESSAGE = "You ate a Re-heated Gummy Polar Bear!"

	private var endTime: Long = -1

	fun init() {
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
	}

	private fun onMessageReceived(message: Component, actionBar: Boolean) {
		if(actionBar) return
		if (message.string == GUMMY_BEAR_MESSAGE) {
			if (endTime < 0)
				endTime = System.currentTimeMillis() + GUMMY_BEAR_LENGTH
			else
				endTime += GUMMY_BEAR_LENGTH
		}
	}

	override fun render(drawContext: GuiGraphics, deltaTicks: Float) {
		if (!isEnabled) return

		val matrixStack = drawContext.pose()
		matrixStack.pushMatrix()
		applyTransformations(matrixStack)

		val timeLeft = ((endTime - System.currentTimeMillis()) / 1000).toInt()
		if(timeLeft < 0 && !config.gummyBearTimer.showExpired) {
			matrixStack.popMatrix()
			return
		}

		val s = if (timeLeft < 0) "§cDepleted" else convertSecondsToTime(timeLeft)
		drawContext.drawString(MC.font, "§fGummy Bear: §a${s}", 0, 0, CommonColors.WHITE)

		matrixStack.popMatrix()
	}

	fun convertSecondsToTime(seconds: Int): String {
		val hours = (seconds / 3600)
		val minutes = (seconds % 3600) / 60
		val remainingSeconds = seconds % 60
		return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
	}
}