package net.wapic.wpcmod.features.dungeons.floor7.terminalhandler

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket

class SelectAllHandler(private val colorName: String): TerminalHandler(TerminalTypes.SELECT) {

    override fun handleSlotUpdate(packet: ScreenHandlerSlotUpdateS2CPacket): Boolean {
        if (packet.slot != type.windowSize - 1) return false
        solution.clear()
        solution.addAll(solveSelectAll(items, colorName))
        return true
    }

    override fun simulateClick(slotIndex: Int, clickType: Int) {
        solution.removeAt(solution.indexOf(slotIndex).takeIf { it != -1 } ?: return)
    }

    private fun solveSelectAll(items: Array<ItemStack?>, color: String): List<Int> {
		items.forEach { itemStack ->
			println("${itemStack?.name?.string} $color")
		}
        return items.mapIndexedNotNull { index, item ->
            if (item?.hasGlint() == false &&
				item.item != Items.BLACK_STAINED_GLASS_PANE &&
				item.name?.string
					?.replace("light blue", "aqua", true)
					?.replace("light gray", "silver", true)
					?.replace("cocoa bean", "brown", true)
					?.replace("ink sac", "black", true)
					?.replace("bone meal", "white", true)
					?.replace("lapis lazuli", "blue", true)
					?.contains(color, true) == true
			) {
				index
			} else {
				null
			}
        }
    }
}