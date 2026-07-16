package net.wapic.wpcmod.features.dungeons.floor7.terminalhandler

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class PanesHandler : TerminalHandler(TerminalTypes.PANES) {

	override fun handleSlotUpdate(syncId: Int, slotId: Int, itemStack: ItemStack): Boolean {
		if (slotId != type.windowSize - 1) return false
        solution.clear()
        solution.addAll(solvePanes(items))
        return true
    }

    override fun simulateClick(slotIndex: Int, clickType: Int) {
        solution.removeAt(solution.indexOf(slotIndex).takeIf { it != -1 } ?: return)
    }

    private fun solvePanes(items: Array<ItemStack?>): List<Int> =
        items.mapIndexedNotNull { index, item -> if (item?.item == Items.STAINED_GLASS_PANE.red) index else null }
}