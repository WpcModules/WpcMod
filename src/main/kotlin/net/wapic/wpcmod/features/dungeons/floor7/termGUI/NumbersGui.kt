package net.wapic.wpcmod.features.dungeons.floor7.termGUI

import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Colors
import net.wapic.wpcmod.features.dungeons.floor7.TerminalSolver
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.util.render.drawText

object NumbersGui : TermGui() {

    override fun renderTerminal(drawContext: DrawContext, slotCount: Int) {
        renderBackground(drawContext, slotCount, 7)

        for (index in 9..slotCount) {
            if ((index % 9).equalsOneOf(0, 8)) continue

            val amount = TerminalSolver.currentTerm?.items?.get(index)?.count?.takeIf { it > 0 } ?: continue
            val solutionIndex = currentSolution.indexOf(index)

            val color = when (solutionIndex) {
                0 -> config.orderColor
                1 -> config.orderColor2
                2 -> config.orderColor3
				else -> config.backgroundColor
            }

			val (slotX, slotY) = renderSlot(drawContext, index, color)

			if (config.showNumbers && solutionIndex != -1) {
				val matrixStack = drawContext.matrices
				val text = amount.toString()

				val slotCenter = (slotSize / 2) * config.customTermSize
				val textScale = config.customTermSize / 1.75f

				val textX = slotX + slotCenter - MC.textRenderer.getWidth(text) * textScale / 2
				val textY = slotY + slotCenter - MC.textRenderer.fontHeight * textScale / 2

				matrixStack.push()
				matrixStack.translate(textX, textY, 0f)
				matrixStack.scale(textScale, textScale, 0f)
				drawContext.drawText(text, 0, 0, Colors.WHITE, true)
				matrixStack.pop()
			}
        }
    }
}