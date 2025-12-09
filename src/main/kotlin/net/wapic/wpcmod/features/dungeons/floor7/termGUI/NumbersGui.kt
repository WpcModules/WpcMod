package net.wapic.wpcmod.features.dungeons.floor7.termGUI

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.util.CommonColors
import net.wapic.wpcmod.features.dungeons.floor7.TerminalSolver
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.util.render.drawText

object NumbersGui : TermGui() {

    override fun renderTerminal(drawContext: GuiGraphics, slotCount: Int) {
        renderBackground(drawContext, slotCount, 7)

        for (index in 9..slotCount) {
            if ((index % 9).equalsOneOf(0, 8)) continue

            val amount = TerminalSolver.currentTerm?.items?.get(index)?.count?.takeIf { it > 0 } ?: continue
            val solutionIndex = currentSolution.indexOf(index)
			if(!solutionIndex.equalsOneOf(0, 1, 2) && !config.showAllOrder) continue

            val color = when (solutionIndex) {
                0 -> config.orderColor
                1 -> config.orderColor2
                2 -> config.orderColor3
				else -> ChromaColour.fromStaticRGB(0, 0, 0, 0)
            }

			val (slotX, slotY) = renderSlot(drawContext, index, color)

			if (config.showNumbers && solutionIndex != -1) {
				val matrixStack = drawContext.pose()
				val text = amount.toString()

				val slotCenter = (slotSize / 2) * config.customTermSize
				val textScale = config.customTermSize / 1.75f

				val textX = slotX + slotCenter - MC.textRenderer.width(text) * textScale / 2
				val textY = slotY + slotCenter - MC.textRenderer.lineHeight * textScale / 2

				matrixStack.pushMatrix()
				matrixStack.translate(textX, textY)
				matrixStack.scale(textScale)
				drawContext.drawText(text, 0, 0, CommonColors.WHITE, true)
				matrixStack.popMatrix()
			}
        }
    }
}