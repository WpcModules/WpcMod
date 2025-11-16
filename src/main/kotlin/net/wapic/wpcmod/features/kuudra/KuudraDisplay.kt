package net.wapic.wpcmod.features.kuudra

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.util.Colors
import net.minecraft.util.Formatting
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.hud.SimpleHudElement
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.KuudraUtils
import net.wapic.wpcmod.util.KuudraUtils.Phase
import net.wapic.wpcmod.util.KuudraUtils.kuudraEntity
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils

object KuudraDisplay : SimpleHudElement("Kuudra Display", 75, 11) {

	private val config get() = WpcMod.config.kuudra
	override val isEnabled: Boolean get() = config.healthDisplay
	override val isActive: Boolean get() = isEnabled && Utils.getLocation() == Island.KUUDRA

	override fun render(drawContext: DrawContext, tickCounter: RenderTickCounter) {
		if (!isActive) return

		val matrixStack = drawContext.matrices
		matrixStack.pushMatrix()
		applyTransformations(matrixStack)

		kuudraEntity?.let {
			if (KuudraUtils.phase == Phase.KILL) {
				val formatting = when {
					it.health > 99000f -> Formatting.GREEN
					it.health > 75000f -> Formatting.DARK_GREEN
					it.health > 50000f -> Formatting.YELLOW
					it.health > 25000f -> Formatting.GOLD
					it.health > 10000f -> Formatting.RED
					else -> Formatting.DARK_RED
				}

				val health = "$formatting${it.health / 1000}K / §a100K §cHP"

				val x = (drawContext.scaledWindowWidth / 2) - (MC.textRenderer.getWidth(health) / 2)
				val y = (drawContext.scaledWindowHeight / 2) - (MC.textRenderer.fontHeight / 2)
				drawContext.drawTextWithShadow(MC.textRenderer, health, x, y, Colors.WHITE)
			}
		}

		matrixStack.popMatrix()
	}
}