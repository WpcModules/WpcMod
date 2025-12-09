package net.wapic.wpcmod.features.dungeons.floor7.termGUI

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.floor7.TerminalSolver
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.render.drawRoundedRect

abstract class TermGui {
    protected val itemIndexMap: MutableMap<Int, Box> = mutableMapOf()
    inline val currentSolution get() = TerminalSolver.currentTerm?.solution.orEmpty()

	val config get() = WpcMod.config.dungeon.floor7.terminalSolvers
	val slotSize: Int = 16

    abstract fun renderTerminal(drawContext: GuiGraphics, slotCount: Int)

    protected fun renderBackground(drawContext: GuiGraphics, slotCount: Int, slotWidth: Int) {
		val matrixStack = drawContext.pose()
		val totalSlotSpace = (slotSize + config.gap) * config.customTermSize

		val width = (slotWidth + 1) * totalSlotSpace
		val height = (slotCount / 9 + 1) * totalSlotSpace
		val x = (drawContext.guiWidth() - width) / 2
		val y = (drawContext.guiHeight() - height) / 2 + getRowOffset(slotCount) * config.customTermSize

		matrixStack.pushMatrix()
		matrixStack.translate(x, y)

		drawContext.drawRoundedRect(0, 0, width.toInt(), height.toInt(), config.backgroundRoundness, config.backgroundColor)

		matrixStack.popMatrix()
    }

	protected fun renderSlot(drawContext: GuiGraphics, index: Int, color: ChromaColour): Pair<Float, Float> {
		val matrixStack = drawContext.pose()
		val scaledSlotSize = slotSize * config.customTermSize
		val totalSlotSpace = scaledSlotSize + config.gap * config.customTermSize

		val x = (index % 9 - 4) * totalSlotSpace + (drawContext.guiWidth() - scaledSlotSize) / 2
		val y = (index / 9 - 2) * totalSlotSpace + (drawContext.guiHeight() - scaledSlotSize) / 2

		itemIndexMap[index] = Box(x, y, scaledSlotSize, scaledSlotSize)

		matrixStack.pushMatrix()
		matrixStack.translate(x, y)
		matrixStack.scale(config.customTermSize)

		drawContext.drawRoundedRect(0, 0, slotSize, slotSize, config.slotRoundness, color)

		matrixStack.popMatrix()
        return x to y
    }

    fun mouseClicked(screen: Screen, button: Int) {
        getHoveredItem()?.let { slot ->
            TerminalSolver.currentTerm?.let {
                if (System.currentTimeMillis() - it.timeOpened >= 350 && it.canClick(slot, button)) {
                    it.click(slot, button, config.hideClicked && !it.isClicked)
					DungeonEvents.TERMINAL_CLICKED.invoker().onClick(screen, slot, button)
                }
            }
        }
    }

    fun closeGui() {

    }

    open fun render(drawContext: GuiGraphics) {
        setCurrentGui(this)
        itemIndexMap.clear()

        renderTerminal(drawContext, TerminalSolver.currentTerm?.type?.windowSize?.minus(10) ?: 0)
    }

	private fun getRowOffset(slotCount: Int): Int {
        return when (slotCount) {
			26 -> -(slotSize / 2)
			44, 54 -> slotSize / 2
			else -> 0
        }
    }

    companion object {
        private var currentGui: TermGui? = null

        fun setCurrentGui(gui: TermGui) {
            currentGui = gui
        }

        fun getHoveredItem(): Int? =
            currentGui?.itemIndexMap?.entries?.find { isAreaHovered(it.value.x, it.value.y, it.value.w, it.value.h) }?.key

		fun isAreaHovered(x: Float, y: Float, w: Float, h: Float): Boolean =
			MC.mouse.xpos() / MC.window.guiScale in x..(x + w) && MC.mouse.ypos() / MC.window.guiScale in y..(y + h)
    }

    data class Box(val x: Float, val y: Float, val w: Float, val h: Float)
}