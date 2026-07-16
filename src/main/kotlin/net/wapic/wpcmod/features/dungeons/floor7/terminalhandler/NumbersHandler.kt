package net.wapic.wpcmod.features.dungeons.floor7.terminalhandler

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class NumbersHandler : TerminalHandler(TerminalTypes.NUMBERS) {

    override fun handleSlotUpdate(syncId: Int, slotId: Int, itemStack: ItemStack): Boolean {
        if (slotId != type.windowSize - 1) return false
        solution.clear()
        solution.addAll(solveNumbers(items))
        return true
    }

    override fun simulateClick(slotIndex: Int, clickType: Int) {
        if (solution.indexOf(slotIndex) == 0) solution.removeAt(0)
    }

    private fun solveNumbers(items: Array<ItemStack?>): List<Int> {
        return items.mapIndexedNotNull { index, item ->
            if (item?.item == Items.STAINED_GLASS_PANE.red) index else null
        }.sortedBy { items[it]?.count }
    }
}