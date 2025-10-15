package net.wapic.wpcmod.features.dungeons.floor7.termGUI

import net.wapic.wpcmod.features.dungeons.floor7.TerminalSolver
import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.gui.DrawContext
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.equalsOneOf
import java.awt.Color

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
                else -> ChromaColour(1f, 0f, 0.2f, 0, 125)
            }

            val (slotX, slotY) = renderSlot(drawContext, index, color.getEffectiveColour())
            val slotSize = 55f * config.customTermSize

            val textX = slotX + (slotSize / 2 - MC.textRenderer.getWidth(amount.toString()) / 2)
            val textY = slotY + (slotSize / 2 - MC.textRenderer.fontHeight / 2)

            if (config.showNumbers && solutionIndex != -1)
                drawContext.drawText(MC.textRenderer, amount.toString(), textX.toInt(), textY.toInt(), Color.WHITE.rgb, true)
        }
    }
}