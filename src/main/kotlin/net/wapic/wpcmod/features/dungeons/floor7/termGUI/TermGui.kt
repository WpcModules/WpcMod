package net.wapic.wpcmod.features.dungeons.floor7.termGUI

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.floor7.TerminalSolver
import net.wapic.wpcmod.util.MC

abstract class TermGui {
    protected val itemIndexMap: MutableMap<Int, Box> = mutableMapOf()
    inline val currentSolution get() = TerminalSolver.currentTerm?.solution.orEmpty()

	val config get() = WpcMod.config.dungeon.floor7.terminalSolvers
	val slotSize: Int = 16

    abstract fun renderTerminal(drawContext: DrawContext, slotCount: Int)

    protected fun renderBackground(drawContext: DrawContext, slotCount: Int, slotWidth: Int) {
		val matrixStack = drawContext.matrices
		val totalSlotSpace = (slotSize + config.gap) * config.customTermSize

		val width = (slotWidth + 1) * totalSlotSpace
		val height = (slotCount / 9 + 1) * totalSlotSpace
		val x = (drawContext.scaledWindowWidth - width) / 2
		val y = (drawContext.scaledWindowHeight - height) / 2 + getRowOffset(slotCount) * config.customTermSize

		matrixStack.push()
		matrixStack.translate(x, y, 0f)

		drawContext.fill(0, 0, width.toInt(), height.toInt(), config.backgroundColor.getEffectiveColourRGB())

		matrixStack.pop()
    }

	protected fun renderSlot(drawContext: DrawContext, index: Int, color: ChromaColour): Pair<Float, Float> {
		val matrixStack = drawContext.matrices
		val scaledSlotSize = slotSize * config.customTermSize
		val totalSlotSpace = scaledSlotSize + config.gap * config.customTermSize

		val x = (index % 9 - 4) * totalSlotSpace + (drawContext.scaledWindowWidth - scaledSlotSize) / 2
		val y = (index / 9 - 2) * totalSlotSpace + (drawContext.scaledWindowHeight - scaledSlotSize) / 2

		itemIndexMap[index] = Box(x, y, scaledSlotSize, scaledSlotSize)

		matrixStack.push()
		matrixStack.translate(x, y, 0f)
		matrixStack.scale(config.customTermSize, config.customTermSize, 0f)

		drawContext.fill(
			0, 0,
			slotSize, slotSize,
			color.getEffectiveColourRGB()
		)

		matrixStack.pop()
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

    open fun render(drawContext: DrawContext) {
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
			MC.mouse.x / MC.window.scaleFactor in x..(x + w) && MC.mouse.y / MC.window.scaleFactor in y..(y + h)
    }

    data class Box(val x: Float, val y: Float, val w: Float, val h: Float)
}