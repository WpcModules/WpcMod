package net.wapic.wpcmod.features.dungeons.floor7.terminalhandler

import net.minecraft.item.ItemStack
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket

class StartsWithHandler(private val letter: String): TerminalHandler(TerminalTypes.STARTS_WITH) {

	private val clickedSlots = mutableSetOf<Int>()
	private var lastContainerId = -1

    override fun handleSlotUpdate(packet: ScreenHandlerSlotUpdateS2CPacket): Boolean {
        if (packet.slot != type.windowSize - 1) return false
        solution.clear()
        solution.addAll(solveStartsWith(items, letter))
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
			if (item?.name?.string?.startsWith(
					letter,
					true
				) == true && index !in clickedSlots
			) index else null
		}
}