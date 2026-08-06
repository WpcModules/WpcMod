package net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator

import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Items

class RubixSimulatorHandler(menu: ChestMenu, title: Component) : TerminalSimulatorHandler(menu) {
	override fun create() {
		this.setSlots { slot ->
			if(slot.index %9 in 3..5 && slot.index / 9 in 1..3) {
				return@setSlots RUBIX_ORDER.random().defaultInstance
			}
			return@setSlots blackPane
		}
	}

	override fun slotClicked(slot: Slot, slotId: Int, buttonNum: Int, containerInput: ContainerInput) {
		val delta = if(buttonNum == 0) 1 else -1
		val index = (RUBIX_ORDER.indexOf(slot.item.item) + delta + RUBIX_ORDER.size) % RUBIX_ORDER.size
		slot.setItem(RUBIX_ORDER[index].defaultInstance)
	}

	override fun onUpdate(slots: List<Slot>) {
		val gameArea = slots.filterNot { it.item.item == Items.BLACK_STAINED_GLASS_PANE }
		if(gameArea.distinctBy { it.item.item }.size == 1) this.onSolve()
	}
}