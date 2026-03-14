package net.wapic.wpcmod.features.dungeons.floor7.termGUI

import net.minecraft.client.gui.GuiGraphics
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.TerminalTypes
import net.wapic.wpcmod.util.Utils.equalsOneOf

object PanesGui : TermGui(TerminalTypes.PANES) {

    override fun renderTerminal(drawContext: GuiGraphics, slotCount: Int) {
		renderBackground(drawContext, slotCount)

        for (index in 9..<slotCount) {
            if ((index % 9).equalsOneOf(0, 1, 7, 8)) continue
            val inSolution = index in currentSolution

			val startColor = if (inSolution) config.panesColor else continue
			renderSlot(drawContext, index, startColor)
        }
    }
}