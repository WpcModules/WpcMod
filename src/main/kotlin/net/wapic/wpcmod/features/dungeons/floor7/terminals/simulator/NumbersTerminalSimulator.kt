package net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator

import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.Items
import net.wapic.wpcmod.features.dungeons.floor7.terminals.NumbersTerminalScreen
import net.wapic.wpcmod.features.dungeons.floor7.terminals.Terminal

class NumbersTerminalSimulator(override val screen: NumbersTerminalScreen) : AbstractTerminalSimulator(screen, Terminal.Type.NUMBERS) {
	private val redPane = ItemStackTemplate(Items.RED_STAINED_GLASS_PANE, emptyNameData)
	private val limePane = ItemStackTemplate(Items.LIME_STAINED_GLASS_PANE, emptyNameData)
	private val counts = mutableListOf(1,2,3,4,5,6,7,8,9,10,11,12,13,14)

	override fun create() {
		counts.shuffle()
		this.setSlots { slot ->
			if(slot.index % 9 in 1..7 && slot.index / 9 in 1..2) {
				return@setSlots redPane.withCount(counts.removeFirst()).create()
			}
			return@setSlots blackPane
		}
	}

	override fun onClick(slotIndex: Int, button: Int) {
		this.setSlots { slot ->
			if(slot.index == slotIndex) limePane.withCount(slot.item.count).create() else slot.item
		}
	}

	override fun onUpdate(items: Array<ItemStack?>) {
		if(items.none(screen::isValidItem)) this.onSolve()
	}
}