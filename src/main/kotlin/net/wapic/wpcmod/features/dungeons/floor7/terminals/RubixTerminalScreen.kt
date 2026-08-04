package net.wapic.wpcmod.features.dungeons.floor7.terminals

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.block.StainedGlassPaneBlock
import net.wapic.wpcmod.WpcMod
import org.lwjgl.glfw.GLFW

class RubixTerminalScreen(menu: ChestMenu, title: Component) : AbstractTerminalScreen(TerminalType.RUBIX, menu, title) {
	private val rubixColorOrder = listOf(DyeColor.ORANGE, DyeColor.YELLOW, DyeColor.GREEN, DyeColor.BLUE, DyeColor.RED)
	private val solutionMap = mutableMapOf<Int, Int>()

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		if (solutionMap.isEmpty()) solve(items)

		for ((slotIndex, clicks) in solutionMap) {
			val color = if (clicks > 0) config.rubixColor2 else config.rubixColor1
			extractSlot(graphics, menu.getSlot(slotIndex), color, clicks.toString())
		}
	}

	override fun slotClicked(slot: Slot, button: Int): Boolean {
		solutionMap[slot.index]?.let { clicks ->
			val button = if (clicks > 0) GLFW.GLFW_MOUSE_BUTTON_LEFT else GLFW.GLFW_MOUSE_BUTTON_RIGHT
			if (doTerminalClick(slot, button)) {
				solutionMap.clear()
				return true
			}
		}
		return false
	}

	private fun solveItem(itemColor: DyeColor, goal: Int): Int? {
		val diff = goal - rubixColorOrder.indexOf(itemColor)

		return when {
			diff == 0 -> null
			diff > 2 -> diff - 5
			diff < -2 -> diff + 5
			else -> diff
		}
	}

	private fun solve(items: Array<Slot?>) {
		var goal: Int? = null

		items.forEach { slot ->
			val itemColor = ((slot?.item?.item as? BlockItem)?.block as? StainedGlassPaneBlock)?.color ?: return@forEach
			val index = rubixColorOrder.indexOf(itemColor)

			if (goal == null) {
				if (index != -1) goal = index
				WpcMod.LOGGER.debug("Goal set to: {}", goal)
				return@forEach
			}

			solveItem(itemColor, goal)?.let { clicks ->
				solutionMap[slot.index] = clicks
				WpcMod.LOGGER.debug("set slot: {} to {}", slot.index, clicks)
			}
		}
	}
}