package net.wapic.wpcmod.features.general

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.hud.SimpleHudElement
import net.wapic.wpcmod.util.MC

object GoonDisplay : SimpleHudElement("Goon Timer", 75, 11) {

	private val config get() = WpcMod.config.general
	override val isEnabled: Boolean get() = config.goonDisplay

	private var endTime: Long = -1

	fun init() {
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
	}

	private fun onMessageReceived(message: Component, actionBar: Boolean) {
		if (message.string == "§e[NPC] Goon§f: Aura thanks you for your cooperation. Come back in 30 minutes if you do not want to face the §oconsequences.")
			endTime = System.currentTimeMillis() + 1800 * 1000
	}

	override fun render(drawContext: GuiGraphics, deltaTicks: Float) {
		if (!isEnabled) return

		val matrixStack = drawContext.pose()
		matrixStack.pushMatrix()
		applyTransformations(matrixStack)

		val timeLeft = ((endTime - System.currentTimeMillis()) / 1000).toInt()
		val s = if (timeLeft < 0) "§cEnded" else convertSecondsToTime(timeLeft)
		drawContext.drawString(MC.textRenderer, "§fGoon Timer: §a${s}", 0, 0, CommonColors.WHITE)

		matrixStack.popMatrix()
	}

	fun convertSecondsToTime(seconds: Int): String {
		val minutes = (seconds % 3600) / 60
		val remainingSeconds = seconds % 60
		return String.format("%02d:%02d", minutes, remainingSeconds)
	}
}
