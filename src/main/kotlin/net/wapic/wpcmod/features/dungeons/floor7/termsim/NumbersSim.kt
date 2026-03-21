package net.wapic.wpcmod.features.dungeons.floor7.termsim

import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.TerminalTypes
import kotlin.math.floor

object NumbersSim : TermSimGUI(
    TerminalTypes.NUMBERS.windowName, TerminalTypes.NUMBERS.windowSize
) {
    override fun create() {
        val used = (1..14).shuffled().toMutableList()
        createNewGui {
            if (floor(it.containerSlot / 9f) in 1f..2f && it.containerSlot % 9 in 1..7) ItemStack(Items.RED_STAINED_GLASS_PANE, used.removeFirst()).apply { set(
				DataComponents.CUSTOM_NAME, Component.literal("")) }
            else blackPane
        }
    }

    override fun slotClick(slot: Slot, button: Int) {
		if (guiInventorySlots.minByOrNull { if (it?.item?.item == Items.RED_STAINED_GLASS_PANE) it.item.count else 1000 } != slot) return
        createNewGui {
            if (it == slot) {
				ItemStack(Items.LIME_STAINED_GLASS_PANE, slot.item.count).apply { set(DataComponents.CUSTOM_NAME, Component.literal("")) }
			} else it.item
        }
        playTermSimSound()
		if (guiInventorySlots.none { it?.item?.item == Items.RED_STAINED_GLASS_PANE }) {
			this@NumbersSim.onTerminalSolved()
		}
    }
}