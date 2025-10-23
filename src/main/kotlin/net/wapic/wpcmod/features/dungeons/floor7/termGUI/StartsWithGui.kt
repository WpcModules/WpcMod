package net.wapic.wpcmod.features.dungeons.floor7.termGUI

import net.minecraft.client.gui.DrawContext
import net.wapic.wpcmod.util.Utils.equalsOneOf

object StartsWithGui : TermGui() {

    override fun renderTerminal(drawContext: DrawContext, slotCount: Int) {
        renderBackground(drawContext, slotCount, 7)

        for (index in 9..slotCount) {
            if ((index % 9).equalsOneOf(0, 8)) continue
            val inSolution = index in currentSolution
			val startColor = if (inSolution) config.startsWithColor else config.backgroundColor
            if (inSolution)
				renderSlot(drawContext, index, startColor.getEffectiveColour())
        }
    }
}