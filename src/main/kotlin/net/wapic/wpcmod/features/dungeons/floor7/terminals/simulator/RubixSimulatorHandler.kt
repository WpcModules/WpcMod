package net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator

import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Items
import net.wapic.wpcmod.features.dungeons.floor7.terminals.Terminal

class RubixSimulatorHandler(menu: ChestMenu, title: Component) : TerminalSimulatorHandler(menu) {

	override fun create() {
		this.setSlots { slot ->
			if (slot.index % 9 in 3..5 && slot.index / 9 in 1..3) {
				return@setSlots Terminal.RUBIX_ORDER.random().defaultInstance
			}
			return@setSlots blackPane
		}
	}

	override fun slotClicked(slot: Slot, slotId: Int, buttonNum: Int, containerInput: ContainerInput) {
		if (slot.item.item == Items.STAINED_GLASS_PANE.black) return
		val delta = if (buttonNum == 0) 1 else -1
		val index =
			(Terminal.RUBIX_ORDER.indexOf(slot.item.item) + delta + Terminal.RUBIX_ORDER.size) % Terminal.RUBIX_ORDER.size
		slot.setItem(Terminal.RUBIX_ORDER[index].defaultInstance)
		playTerminalSound()
	}

	override fun isTerminalSolved(slots: List<Slot>): Boolean {
		val gameArea = slots.filterNot { it.item.item == Items.STAINED_GLASS_PANE.black }
		return gameArea.distinctBy { it.item.item }.size == 1
	}
}