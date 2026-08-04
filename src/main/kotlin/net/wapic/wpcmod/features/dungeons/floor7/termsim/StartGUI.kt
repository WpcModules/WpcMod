package net.wapic.wpcmod.features.dungeons.floor7.termsim

import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

object StartGUI : TermSimGUI("Terminal Simulator", 27) {
    private val rubixButton = ItemStack(Items.RED_DYE).apply {
        set(
            DataComponents.CUSTOM_NAME,
            Component.literal("§6Change all to same color!")
        )
    }

    override fun create() {
        createNewGui {
            if (it.containerSlot == 13) rubixButton else blackPane
        }
    }

    override fun slotClick(slot: Slot, button: Int) {
        if (slot.container is Inventory) return
        if (slot.containerSlot == 13) RubixSim.open(ping)
    }
}