package net.wapic.wpcmod.features.dungeons.floor7.termGUI

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.util.CommonColors
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.render.drawText

object RubixGui : TermGui() {

    override fun renderTerminal(drawContext: GuiGraphics, slotCount: Int) {
        renderBackground(drawContext, slotCount, 3)

        currentSolution.distinct().forEach { index ->
            val amount = currentSolution.count { it == index }
            val clicksRequired = if (amount < 3) amount else amount - 5
            if (clicksRequired == 0) return@forEach

			val color = when (clicksRequired) {
				1 -> config.rubixColor1
				2 -> config.rubixColor2
				-1 -> config.oppositeRubixColor1
				else -> config.oppositeRubixColor2
			}

			val (slotX, slotY) = renderSlot(drawContext, index, color)

			val text = clicksRequired.toString()
			val slotCenter = (slotSize / 2) * config.customTermSize
			val textScale = config.customTermSize / 1.75f

			val textX = slotX + slotCenter - MC.textRenderer.width(text) * textScale / 2
			val textY = slotY + slotCenter - MC.textRenderer.lineHeight * textScale / 2

			val matrixStack = drawContext.pose()

			matrixStack.pushMatrix()
			matrixStack.translate(textX, textY)
			matrixStack.scale(textScale)
			drawContext.drawText(text, 0, 0, CommonColors.WHITE, true)
			matrixStack.popMatrix()
        }
    }
}