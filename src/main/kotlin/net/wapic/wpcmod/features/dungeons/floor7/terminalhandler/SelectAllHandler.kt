package net.wapic.wpcmod.features.dungeons.floor7.terminalhandler

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
import net.minecraft.util.DyeColor

class SelectAllHandler(private val color: DyeColor): TerminalHandler(TerminalTypes.SELECT) {

    override fun handleSlotUpdate(packet: ScreenHandlerSlotUpdateS2CPacket): Boolean {
        if (packet.slot != type.windowSize - 1) return false
        solution.clear()
        solution.addAll(solveSelectAll(items, color))
        return true
    }

    override fun simulateClick(slotIndex: Int, clickType: Int) {
        solution.removeAt(solution.indexOf(slotIndex).takeIf { it != -1 } ?: return)
    }

    private fun solveSelectAll(items: Array<ItemStack?>, color: DyeColor): List<Int> {
		return items.mapIndexedNotNull { index, itemStack ->
			if (itemStack?.hasGlint() == false &&
				itemStack.item != Items.BLACK_STAINED_GLASS_PANE &&
				(itemStack.item.name.string.startsWith(color.id.replace("_", " "), true) || when(color) {
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