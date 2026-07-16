package net.wapic.wpcmod.features.dungeons.floor7.termsim

import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.TerminalTypes
import kotlin.math.floor

object PanesSim : TermSimGUI(
    TerminalTypes.PANES.windowName, TerminalTypes.PANES.windowSize
) {
    private val greenPane get() = ItemStack(Items.STAINED_GLASS_PANE.lime).apply { set(DataComponents.CUSTOM_NAME, Component.literal("")) }
    private val redPane   get() = ItemStack(Items.STAINED_GLASS_PANE.red).apply { set(DataComponents.CUSTOM_NAME, Component.literal("")) }

    override fun create() {
        createNewGui {
            if (floor(it.containerSlot / 9f) in 1f..3f && it.containerSlot % 9 in 2..6) {
				if (Math.random() > 0.75) greenPane else redPane
			} else blackPane
        }
    }

    override fun slotClick(slot: Slot, button: Int) {
        createNewGui {
			if (it == slot) {
				if (slot.item.item == Items.STAINED_GLASS_PANE.red) greenPane else redPane
			} else it.item
		}

        playTermSimSound()
		if (guiInventorySlots.none { it?.item?.item == Items.STAINED_GLASS_PANE.red }) {
			this@PanesSim.onTerminalSolved()
		}
    }
}