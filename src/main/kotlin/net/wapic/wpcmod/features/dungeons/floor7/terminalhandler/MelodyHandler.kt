package net.wapic.wpcmod.features.dungeons.floor7.terminalhandler

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class MelodyHandler : TerminalHandler(TerminalTypes.MELODY) {

    override fun handleSlotUpdate(syncId: Int, slotId: Int, itemStack: ItemStack): Boolean {
        solution.clear()
        solution.addAll(solveMelody(items))
        return true
    }

    override fun click(slotIndex: Int, button: Int, simulateClick: Boolean) {
        val greenPane = items.indexOfLast { it?.item == Items.STAINED_GLASS_PANE.lime }.takeIf { it != -1 }
        val magentaPane = items.indexOfFirst { it?.item == Items.STAINED_GLASS_PANE.magenta }.takeIf { it != -1 }
        if (greenPane?.rem(9) != magentaPane?.rem(9)) return
        super.click(slotIndex, button, simulateClick)
    }

    private fun solveMelody(items: Array<ItemStack?>): List<Int> {
        val greenPane = items.indexOfLast { it?.item == Items.STAINED_GLASS_PANE.lime }.takeIf { it != -1 } ?: return emptyList()
        val magentaPane = items.indexOfFirst { it?.item == Items.STAINED_GLASS_PANE.magenta }.takeIf { it != -1 } ?: return emptyList()
        val greenClay = items.indexOfLast { it?.item == Items.DYED_TERRACOTTA.lime }.takeIf { it != -1 } ?: return emptyList()
        return items.mapIndexedNotNull { index, item ->
            when {
                index == greenPane || item?.item == Items.STAINED_GLASS_PANE.magenta -> index
                index == greenClay && greenPane % 9 == magentaPane % 9 -> index
                else -> null
            }
        }
    }
}