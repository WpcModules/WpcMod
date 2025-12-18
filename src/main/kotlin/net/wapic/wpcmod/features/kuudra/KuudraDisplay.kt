package net.wapic.wpcmod.features.kuudra

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.util.CommonColors
import net.minecraft.ChatFormatting
import net.minecraft.client.DeltaTracker
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.hud.SimpleHudElement
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.KuudraUtils
import net.wapic.wpcmod.util.KuudraUtils.Phase
import net.wapic.wpcmod.util.KuudraUtils.kuudraEntity
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.Utils.toFixed

object KuudraDisplay : SimpleHudElement("Kuudra Display", 75, 11) {

	private val config get() = WpcMod.config.kuudra
	override val isEnabled: Boolean get() = config.healthDisplay
	override val isActive: Boolean get() = isEnabled && Utils.getLocation() == Island.KUUDRA

	override fun render(drawContext: GuiGraphics, tickCounter: DeltaTracker) {
		if (!isActive) return

		val matrixStack = drawContext.pose()
		matrixStack.pushMatrix()
		applyTransformations(matrixStack)

		kuudraEntity?.let {
			if (KuudraUtils.phase == Phase.KILL) {
				val formatting = when {
					it.health > 99000f -> ChatFormatting.GREEN
					it.health > 75000f -> ChatFormatting.DARK_GREEN
					it.health > 50000f -> ChatFormatting.YELLOW
					it.health > 25000f -> ChatFormatting.GOLD
					it.health > 10000f -> ChatFormatting.RED
					else -> ChatFormatting.DARK_RED
				}

				val health = "$formatting${(it.health / 1000).toFixed(2)}K / §a100K §cHP"

				drawContext.drawString(MC.textRenderer, health, 0, 0, CommonColors.WHITE)
			}
		}

		matrixStack.popMatrix()
	}
}