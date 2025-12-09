package net.wapic.wpcmod.features.dungeons.floor7.termGUI

import net.minecraft.client.gui.GuiGraphics
import net.wapic.wpcmod.util.Utils.equalsOneOf

object StartsWithGui : TermGui() {

    override fun renderTerminal(drawContext: GuiGraphics, slotCount: Int) {
        renderBackground(drawContext, slotCount, 7)

        for (index in 9..slotCount) {
			if ((index % 9).equalsOneOf(0, 8) || index !in currentSolution) continue
			renderSlot(drawContext, index, config.startsWithColor)
        }
    }
}