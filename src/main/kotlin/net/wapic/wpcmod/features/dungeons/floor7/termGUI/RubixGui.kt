package net.wapic.wpcmod.features.dungeons.floor7.termGUI

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.gui.DrawContext
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.render.drawText
import java.awt.Color

object RubixGui : TermGui() {

    override fun renderTerminal(drawContext: DrawContext, slotCount: Int) {
        renderBackground(drawContext, slotCount, 3)

        currentSolution.distinct().forEach { index ->
            val amount = currentSolution.count { it == index }
            val clicksRequired = if (amount < 3) amount else amount - 5
            if (clicksRequired == 0) return@forEach

            val (slotX, slotY) = renderSlot(drawContext, index, getColor(clicksRequired))
			val slotSize = 40f * config.customTermSize
			val fontSize = 10f * config.customTermSize

            val textX = slotX + (slotSize - MC.textRenderer.getWidth(clicksRequired.toString())) / 2f
            val textY = slotY + (slotSize + fontSize) / 2f - fontSize * 0.9f

			drawContext.drawText(clicksRequired.toString(), textX.toInt(), textY.toInt(), Color.WHITE.rgb, true)
        }
    }

	private fun getColor(clicksRequired: Int): ChromaColour = when (clicksRequired) {
        1 -> config.rubixColor1
        2 -> config.rubixColor2
        -1 -> config.oppositeRubixColor1
        else -> config.oppositeRubixColor2
	}
}