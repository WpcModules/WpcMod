package net.wapic.wpcmod.features.dungeons.floor7.termsim

import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.wapic.wpcmod.features.dungeons.floor7.TerminalSimulator.openRandomTerminal

object StartGUI : TermSimGUI("Terminal Simulator", 27) {
    private val termItems = listOf(
        ItemStack(Items.PURPLE_DYE).apply { set(DataComponents.CUSTOM_NAME, Component.literal("§aCorrect all the panes!")) },
        ItemStack(Items.RED_DYE).apply { set(DataComponents.CUSTOM_NAME, Component.literal("§6Change all to same color!")) },
        ItemStack(Items.PINK_DYE).apply { set(DataComponents.CUSTOM_NAME, Component.literal("§3Click in order!")) },
        ItemStack(Items.LIME_DYE).apply { set(DataComponents.CUSTOM_NAME, Component.literal("§5What starts with: \"*\"?")) },
        ItemStack(Items.BROWN_DYE).apply { set(DataComponents.CUSTOM_NAME, Component.literal("§bSelect all the \"*\" items!")) },
        ItemStack(Items.CYAN_DYE).apply { set(DataComponents.CUSTOM_NAME, Component.literal("§dClick the button on time!")) }
    )
    private val randomButton = ItemStack(Items.WHITE_DYE).apply { set(DataComponents.CUSTOM_NAME, Component.literal("§7Random")) }

    override fun create() {
        createNewGui {
            when (it.containerSlot) {
                13  -> randomButton
                in 10..12 -> termItems[it.containerSlot - 10]
                in 14..16 -> termItems[it.containerSlot - 11]
                else -> blackPane
            }
        }
    }

    override fun slotClick(slot: Slot, button: Int) {
        if (slot.container is Inventory) return

        when (slot.containerSlot) {
            10 -> PanesSim.open(ping)
            11 -> RubixSim.open(ping)
            12 -> NumbersSim.open(ping)
            13 -> openRandomTerminal(ping)
            14 -> StartsWithSim().open(ping)
            15 -> SelectAllSim().open(ping)
            16 -> MelodySim.open(ping)
        }
    }
}