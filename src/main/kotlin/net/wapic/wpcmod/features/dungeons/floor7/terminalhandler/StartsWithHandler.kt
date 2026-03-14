package net.wapic.wpcmod.features.dungeons.floor7.terminalhandler

import net.minecraft.world.item.ItemStack
import net.wapic.wpcmod.util.ChatUtils

class StartsWithHandler(val letter: String) : TerminalHandler(TerminalTypes.STARTS_WITH) {

	private val clickedSlots = mutableSetOf<Int>()
	private var lastContainerId = -1

	override fun handleSlotUpdate(syncId: Int, slotId: Int, itemStack: ItemStack): Boolean {
		if (slotId != type.windowSize - 1) return false
		val solvedItems = solveStartsWith(items, letter)
		if (solvedItems.isEmpty() && clickedSlots.isEmpty()) {
			ChatUtils.sendMessage("No items found starting with '$letter'")
		}
        solution.clear()
		solution.addAll(solvedItems)
        return true
    }

	override fun click(slotIndex: Int, button: Int, simulateClick: Boolean) {
		if (canClick(slotIndex, button) && lastContainerId != containerId) {
			clickedSlots.add(slotIndex)
			lastContainerId = containerId
		}

		super.click(slotIndex, button, simulateClick)
	}

    override fun simulateClick(slotIndex: Int, clickType: Int) {
        solution.removeAt(solution.indexOf(slotIndex).takeIf { it != -1 } ?: return)
    }

    private fun solveStartsWith(items: Array<ItemStack?>, letter: String): List<Int> =
		items.mapIndexedNotNull { index, item ->
			if (item?.hoverName?.string?.startsWith(letter, true) == true && index !in clickedSlots) index else null
		}
}