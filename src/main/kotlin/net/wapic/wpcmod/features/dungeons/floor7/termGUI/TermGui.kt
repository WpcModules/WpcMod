package net.wapic.wpcmod.features.dungeons.floor7.termGUI

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.floor7.TerminalSolver
import net.wapic.wpcmod.util.MC
import java.awt.Color
import kotlin.math.ceil
import kotlin.math.floor

abstract class TermGui {
    protected val itemIndexMap: MutableMap<Int, Box> = mutableMapOf()
    inline val currentSolution get() = TerminalSolver.currentTerm?.solution.orEmpty()

	val config get() = WpcMod.config.dungeon.floor7.terminalSolvers

    abstract fun renderTerminal(drawContext: DrawContext, slotCount: Int)

    protected fun renderBackground(drawContext: DrawContext, slotCount: Int, slotWidth: Int) {
		val slotSize = 40f * config.customTermSize
        val gap = config.gap * config.customTermSize
        val totalSlotSpace = slotSize + gap

        val backgroundStartX = (drawContext.scaledWindowWidth / 2f + -(slotWidth / 2f) * totalSlotSpace - 7.5f * config.customTermSize).toInt()
        val backgroundStartY = (drawContext.scaledWindowHeight / 2f + ((-getRowOffset(slotCount) + 0.5f) * totalSlotSpace) - 7.5f * config.customTermSize).toInt()
        val backgroundWidth = (slotWidth * totalSlotSpace + 15f * config.customTermSize).toInt()
        val backgroundHeight = (((slotCount) / 9) * totalSlotSpace + 15f * config.customTermSize).toInt()

        drawContext.fill(backgroundStartX, backgroundStartY, backgroundStartX + backgroundWidth,backgroundStartY + backgroundHeight, config.backgroundColor.getEffectiveColourRGB())
    }

    protected fun renderSlot(drawContext: DrawContext, index: Int, startColor: Color): Pair<Float, Float> {
		val slotSize = 40f * config.customTermSize
        val totalSlotSpace = slotSize + config.gap * config.customTermSize

        val x = (index % 9 - 4) * totalSlotSpace + drawContext.scaledWindowWidth / 2f - slotSize / 2
        val y = (index / 9 - 2) * totalSlotSpace + drawContext.scaledWindowHeight / 2f - slotSize / 2

        itemIndexMap[index] = Box(x, y, slotSize, slotSize)

        drawContext.fill(floor(x).toInt(), floor(y).toInt(), floor(x).toInt() + ceil(slotSize).toInt(), floor(y).toInt() + ceil(slotSize).toInt(), startColor.rgb)
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

    private fun getRowOffset(slotCount: Int): Float {
        return when (slotCount) {
            in 0..9 -> 0f
            in 10..18 -> 1f
            in 19..27 -> 2f
            in 28..36 -> 2f
            in 37..45 -> 2f
            else -> 3f
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