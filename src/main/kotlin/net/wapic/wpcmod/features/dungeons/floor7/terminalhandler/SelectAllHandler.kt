package net.wapic.wpcmod.features.dungeons.floor7.terminalhandler

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket

class SelectAllHandler(private val colorName: String): TerminalHandler(TerminalTypes.SELECT) {

	// Hypixel uses legacy dyes to be compatible with 1.8.9 with this map we can get the modern equivalence of the item
	private val legacyDyeMap = mapOf(
		Items.COCOA_BEANS to Items.BROWN_DYE,
		Items.BONE_MEAL to Items.WHITE_DYE,
		Items.LAPIS_LAZULI to Items.BLUE_DYE,
		Items.INK_SAC to Items.BLACK_DYE,
		Items.WHITE_WOOL to Items.WHITE_DYE, // White wool has the Display name Wool. So we need to make it any other white item
	)

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
		return items.mapIndexedNotNull { index, itemStack ->
			val itemName = legacyDyeMap[itemStack?.item]?.defaultStack?.name?.string ?: itemStack?.name?.string

			if (itemStack?.hasGlint() == false &&
				itemStack.item != Items.BLACK_STAINED_GLASS_PANE &&
				itemName?.replace("light gray", "silver", true)?.replace("light blue", "auqa", true)?.contains(color, true) == true
			) {
				index
			} else {
				null
			}
        }
    }
}