package net.wapic.wpcmod.features.dungeons.floor7.terminals

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class RubixTerminalScreen(menu: ChestMenu, title: Component) : AbstractTerminalScreen(menu, title) {

	override val gameWidth: Int = 3
	override val gameHeight: Int = 3

	private val gameArea = listOf(12, 13, 14, 21, 22, 23, 30, 31, 32)
	private var goal: Int? = null

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		gameArea.forEachIndexed { index, slotIndex ->
			val clicks = solution.getOrNull(index) ?: return@forEachIndexed
			val color = when (clicks) {
				1 -> config.rubixColor1
				2 -> config.rubixColor2
				-1 -> config.oppositeRubixColor1
				-2 -> config.oppositeRubixColor2
				else -> return@forEachIndexed
			}
			extractSlot(graphics, slotIndex, color, clicks.toString())
		}
	}

	override fun slotClicked(slotIndex: Int, button: Int, input: ContainerInput): Boolean {
		val index = gameArea.indexOf(slotIndex).takeIf { it != -1 } ?: return false
		solution.getOrNull(index)?.let { clicks ->
			if (clicks == 0) return false
			val button = if (clicks > 0) InputConstants.MOUSE_BUTTON_LEFT else InputConstants.MOUSE_BUTTON_RIGHT
			solution[index] = clicks - if (button == 0) 1 else -1
			doTerminalClick(slotIndex, button, ContainerInput.PICKUP)
			return true
		}

		return false
	}

	override fun isExpected(slotIndex: Int, itemStack: ItemStack): Boolean {
		return getClicks(Terminal.RUBIX_ORDER.indexOf(itemStack.item), goal ?: return false) == 0
	}

	private fun getClicks(start: Int, goal: Int): Int {
		val size = Terminal.RUBIX_ORDER.size
		return Math.floorMod(goal - start + size / 2, size) - size / 2
	}

	override fun solveTerminal(slots: List<Slot>): List<Int> {
		val gameArea = slots.filterNot { it.item.item == Items.STAINED_GLASS_PANE.black }
		if (goal == null) {
			val goalItem = gameArea.groupingBy { it.item.item }.eachCount().maxBy { it.value }.key
			goal = Terminal.RUBIX_ORDER.indexOf(goalItem)
		}

		return gameArea.map { getClicks(Terminal.RUBIX_ORDER.indexOf(it.item.item), goal ?: return emptyList()) }
	}
}