package net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator

import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.Items

class NumbersSimulatorHandler(menu: ChestMenu, title: Component) : TerminalSimulatorHandler(menu) {
	private val redPane = ItemStackTemplate(Items.RED_STAINED_GLASS_PANE)
	private val limePane = ItemStackTemplate(Items.LIME_STAINED_GLASS_PANE)
	private val counts = mutableListOf(1,2,3,4,5,6,7,8,9,10,11,12,13,14)

	override fun create() {
		counts.shuffle()

		this.setSlots { slot ->
			if(slot.index % 9 in 1..7 && slot.index / 9 in 1..2) {
				return@setSlots redPane.apply(counts.removeFirst(), emptyNameData)
			}
			return@setSlots blackPane
		}
	}

	override fun slotClicked(slot: Slot, slotId: Int, buttonNum: Int, containerInput: ContainerInput) {
		if (slot.item.item != Items.RED_STAINED_GLASS_PANE) return
		if (slot.item.count != counts.size + 1) return
		slot.setItem(limePane.apply(slot.item.count, emptyNameData))
		counts.add(slot.item.count)
	}

	override fun onUpdate(slots: List<Slot>) {
		if(slots.none { it.item.item == Items.RED_STAINED_GLASS_PANE } ) this.onSolve()
	}
}