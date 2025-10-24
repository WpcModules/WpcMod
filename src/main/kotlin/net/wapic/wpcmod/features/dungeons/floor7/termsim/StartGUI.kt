package net.wapic.wpcmod.features.dungeons.floor7.termsim

import net.minecraft.component.DataComponentTypes
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.slot.Slot
import net.minecraft.text.Text
import net.wapic.wpcmod.features.dungeons.floor7.TerminalSimulator.openRandomTerminal

object StartGUI : TermSimGUI("Terminal Simulator", 27) {
    private val termItems = listOf(
        ItemStack(Items.PURPLE_DYE).apply { set(DataComponentTypes.CUSTOM_NAME, Text.literal("§aCorrect all the panes!")) },
        ItemStack(Items.RED_DYE).apply { set(DataComponentTypes.CUSTOM_NAME, Text.literal("§6Change all to same color!")) },
        ItemStack(Items.PINK_DYE).apply { set(DataComponentTypes.CUSTOM_NAME, Text.literal("§3Click in order!")) },
        ItemStack(Items.LIME_DYE).apply { set(DataComponentTypes.CUSTOM_NAME, Text.literal("§5What starts with: \"*\"?")) },
        ItemStack(Items.BROWN_DYE).apply { set(DataComponentTypes.CUSTOM_NAME, Text.literal("§bSelect all the \"*\" items!")) },
        ItemStack(Items.CYAN_DYE).apply { set(DataComponentTypes.CUSTOM_NAME, Text.literal("§dClick the button on time!")) }
    )
    private val randomButton = ItemStack(Items.WHITE_DYE).apply { set(DataComponentTypes.CUSTOM_NAME, Text.literal("§7Random")) }

    override fun create() {
        createNewGui {
            when (it.index) {
                13  -> randomButton
                in 10..12 -> termItems[it.index - 10]
                in 14..16 -> termItems[it.index - 11]
                else -> blackPane
            }
        }
    }

    override fun slotClick(slot: Slot, button: Int) {
        when (slot.index) {
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