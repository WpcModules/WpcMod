package net.wapic.wpcmod.features.dungeons.floor7.terminalhandler

import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class SelectAllHandler(val color: DyeColor) : TerminalHandler(TerminalTypes.SELECT_ALL) {

	override fun handleSlotUpdate(syncId: Int, slotId: Int, itemStack: ItemStack): Boolean {
		if (slotId != type.windowSize - 1) return false
        solution.clear()
        solution.addAll(solveSelectAll(items, color))
        return true
    }

    override fun simulateClick(slotIndex: Int, clickType: Int) {
        solution.removeAt(solution.indexOf(slotIndex).takeIf { it != -1 } ?: return)
    }

    private fun solveSelectAll(items: Array<ItemStack?>, color: DyeColor): List<Int> {
		return items.mapIndexedNotNull { index, itemStack ->
			if (itemStack?.hasFoil() == false &&
				itemStack.item != Items.BLACK_STAINED_GLASS_PANE &&
				(itemStack.item.name.string.startsWith(color.name.replace("_", " "), true) || when(color) {
					DyeColor.BLACK -> itemStack.item == Items.INK_SAC
					DyeColor.BLUE -> itemStack.item == Items.LAPIS_LAZULI
					DyeColor.WHITE -> itemStack.item == Items.BONE_MEAL
					DyeColor.BROWN -> itemStack.item == Items.COCOA_BEANS
					else -> false
				})
			) {
				index
			} else {
				null
			}
        }
    }
}