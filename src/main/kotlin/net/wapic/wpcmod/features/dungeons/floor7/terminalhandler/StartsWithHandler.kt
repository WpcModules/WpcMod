package net.wapic.wpcmod.features.dungeons.floor7.terminalhandler

import net.minecraft.world.item.ItemStack

class StartsWithHandler(val letter: String) : TerminalHandler(TerminalTypes.STARTS_WITH) {

	val clickedSlots = mutableSetOf<Int>()

	override fun handleSlotUpdate(syncId: Int, slotId: Int, itemStack: ItemStack): Boolean {
		if (slotId != type.windowSize - 1) return false
        solution.clear()
		solution.addAll(solveStartsWith(items, letter))
        return true
    }

	override fun click(slotIndex: Int, button: Int, simulateClick: Boolean) {
		if (canClick(slotIndex, button)) {
			clickedSlots.add(slotIndex)
		}

		super.click(slotIndex, button, simulateClick)
	}

    override fun simulateClick(slotIndex: Int, clickType: Int) {
        solution.removeAt(solution.indexOf(slotIndex).takeIf { it != -1 } ?: return)
    }

    private fun solveStartsWith(items: Array<ItemStack?>, letter: String): List<Int> =
		items.mapIndexedNotNull { index, itemStack ->
			if (index in clickedSlots) return@mapIndexedNotNull null
			val startsWithLetter = itemStack?.hoverName?.string?.startsWith(letter, true) == true

			return@mapIndexedNotNull if (startsWithLetter) index else null
		}
}