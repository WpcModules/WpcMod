package net.wapic.wpcmod.features.dungeons.floor7.termGUI

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.gui.DrawContext
import net.wapic.wpcmod.features.dungeons.floor7.TerminalSolver
import net.wapic.wpcmod.util.Utils.equalsOneOf

object MelodyGui : TermGui() {

    override fun render(drawContext: DrawContext) {
        setCurrentGui(this)
        itemIndexMap.clear()

        renderTerminal(drawContext, TerminalSolver.currentTerm?.type?.windowSize ?: 0)
    }

    override fun renderTerminal(drawContext: DrawContext, slotCount: Int) {
        renderBackground(drawContext, slotCount, 7)

        TerminalSolver.currentTerm?.items?.forEachIndexed { index, item ->
            if ((index % 9).equalsOneOf(0, 6, 8) || ((index / 9).equalsOneOf(0, 6) && index % 9 == 7)) return@forEachIndexed
            if ((index !in 9 until 45) && !currentSolution.contains(index)) return@forEachIndexed

            val color = when {
                currentSolution.contains(index) -> {
                    when {
                        (index % 9) in 1..5 && index / 9 != 0 && index / 9 != 5 -> config.melodyPointerColor
                        index / 9 == 0 || index / 9 == 5 -> config.melodyColumColor

                        else -> config.melodyPointerColor
                    }
                }
                (index % 9) in 1..5 && (index / 9).equalsOneOf(1, 2, 3, 4) && currentSolution.any { it / 9 == index / 9 } ->
                    config.melodyRowColor
                else -> ChromaColour(1f, 0.1f, 1f, 0, 255)
            }

			renderSlot(drawContext, index, color)
        }
    }
}