package net.wapic.wpcmod.features.dungeons.floor7.termGUI

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.util.CommonColors
import net.minecraft.util.Util
import net.minecraft.world.item.Items
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.floor7.TerminalSolver
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.SelectAllHandler
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.StartsWithHandler
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.TerminalHandler
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.TerminalTypes
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.render.drawRoundedRect

abstract class TermGui(val type: TerminalTypes) {
    protected val itemIndexMap: MutableMap<Int, Box> = mutableMapOf()
    inline val currentSolution get() = TerminalSolver.currentTerm?.solution.orEmpty()

	val config get() = WpcMod.config.dungeon.floor7.terminalSolvers

	abstract fun renderTerminal(drawContext: GuiGraphicsExtractor, slotCount: Int)

	protected fun renderBackground(drawContext: GuiGraphicsExtractor, slotCount: Int) {
		val matrixStack = drawContext.pose()
		val totalSlotSpace = (SLOT_SIZE + config.gap) * config.customTermSize

		val width = (type.width + 0.5f) * totalSlotSpace
		val height = (slotCount / 9 + 0.5f) * totalSlotSpace
		val x = (drawContext.guiWidth() - width) / 2
		val y = (drawContext.guiHeight() - height) / 2 + getRowOffset(slotCount) * config.customTermSize

		matrixStack.pushMatrix()
		matrixStack.translate(x, y)

		drawContext.drawRoundedRect(0, 0, width.toInt(), height.toInt(), config.backgroundRoundness, config.backgroundColor)

		matrixStack.popMatrix()
    }

	protected fun renderSlot(drawContext: GuiGraphicsExtractor, index: Int, color: ChromaColour): Pair<Float, Float> {
		val matrixStack = drawContext.pose()
		val scaledSlotSize = SLOT_SIZE * config.customTermSize
		val totalSlotSpace = scaledSlotSize + config.gap * config.customTermSize

		val x = (index % 9 - 4) * totalSlotSpace + (drawContext.guiWidth() - scaledSlotSize) / 2
		val y = (index / 9 - 2) * totalSlotSpace + (drawContext.guiHeight() - scaledSlotSize) / 2

		itemIndexMap[index] = Box(x, y, scaledSlotSize, scaledSlotSize)

		matrixStack.pushMatrix()
		matrixStack.translate(x, y)
		matrixStack.scale(config.customTermSize)

		drawContext.drawRoundedRect(0, 0, SLOT_SIZE, SLOT_SIZE, config.slotRoundness, color)

		matrixStack.popMatrix()
        return x to y
    }

	protected fun renderDebug(drawContext: GuiGraphicsExtractor, currentHandler: TerminalHandler) {
		val x = 2
		val y = 2

		val lines = buildList {
			when (type) {
				TerminalTypes.STARTS_WITH -> add("${type.name} '${(currentHandler as StartsWithHandler).letter}' Debug Info")
				TerminalTypes.SELECT_ALL -> add("${type.name} '${(currentHandler as SelectAllHandler).color}' Debug Info")
				else -> add("${type.name} Debug Info")
			}
			add("Time open: ${Util.getMillis() - currentHandler.timeOpened}ms")
			add("Is Clicked: ${currentHandler.isClicked}")
			add("Solution: ${currentHandler.solution.joinToString()}")
		}

		for ((index, line) in lines.withIndex()) {
			drawContext.text(MC.font, line, x, y + index * 10, CommonColors.WHITE, true)
		}

		drawContext.text(MC.font, "Items in terminal:", x, y + lines.size * 10, CommonColors.WHITE, true)
		var itemIndex = 1
		currentHandler.items.forEachIndexed { index, stack ->
			if (stack == null || stack.item == Items.BLACK_STAINED_GLASS_PANE) return@forEachIndexed
			drawContext.text(
				MC.font,
				"${if (index in currentSolution) "§a" else "§c"}${stack.hoverName.string}",
				x,
				y + lines.size * 12 + itemIndex * 10,
				CommonColors.WHITE,
				true
			)
			itemIndex++
		}
	}

	fun mouseClicked(screen: Screen, button: Int, handler: TerminalHandler) {
        getHoveredItem()?.let { slot ->
			if (Util.getMillis() - handler.timeOpened >= 350 && handler.canClick(slot, button)) {
				handler.click(slot, button, config.hideClicked && !handler.isClicked)
				DungeonEvents.TERMINAL_CLICKED.invoker().onClick(screen, slot, button)
			}
		}
	}

	open fun render(drawContext: GuiGraphicsExtractor, currentHandler: TerminalHandler) {
        setCurrentGui(this)
        itemIndexMap.clear()

		if (config.debug) renderDebug(drawContext, currentHandler)
		renderTerminal(drawContext, type.windowSize - 10)
    }

	private fun getRowOffset(slotCount: Int): Int {
        return when (slotCount) {
			26 -> -(SLOT_SIZE / 2)
			44, 54 -> SLOT_SIZE / 2
			else -> 0
        }
    }

    companion object {
        private var currentGui: TermGui? = null
		protected const val SLOT_SIZE: Int = 16

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