package net.wapic.wpcmod.features.dungeons.floor7.termGUI

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.TerminalTypes
import net.wapic.wpcmod.util.Utils.equalsOneOf

object StartsWithGui : TermGui(TerminalTypes.STARTS_WITH) {

	override fun renderTerminal(drawContext: GuiGraphicsExtractor, slotCount: Int) {
		renderBackground(drawContext, slotCount)

        for (index in 9..slotCount) {
			if ((index % 9).equalsOneOf(0, 8)) continue
			val inSolution = index in currentSolution
			if (inSolution) renderSlot(drawContext, index, config.startsWithColor)
        }
    }
}