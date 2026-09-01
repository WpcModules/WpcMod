package net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator

import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.Items
import kotlin.random.Random

class PanesSimulatorHandler(menu: ChestMenu, title: Component) : TerminalSimulatorHandler(menu) {
	private val redPane = ItemStackTemplate(Items.RED_STAINED_GLASS_PANE, emptyNameData)
	private val limePane = ItemStackTemplate(Items.LIME_STAINED_GLASS_PANE, emptyNameData)

	override fun create() {
		this.setSlots { slot ->
			if (slot.index % 9 in 2..6 && slot.index / 9 in 1..3) {
				return@setSlots if (Random.nextDouble() < 0.65) redPane.create() else limePane.create()
			}
			blackPane
		}
	}

	override fun slotClicked(slot: Slot, slotId: Int, buttonNum: Int, containerInput: ContainerInput) {
		if (slotId % 9 !in 2..6 && slotId / 9 !in 1..3) return
		when (slot.item.item) {
			Items.RED_STAINED_GLASS_PANE -> slot.setItem(limePane.create())
			Items.LIME_STAINED_GLASS_PANE -> slot.setItem(redPane.create())
		}
		playTerminalSound()
	}

	override fun isTerminalSolved(slots: List<Slot>): Boolean {
		return slots.none { it.item.item == Items.RED_STAINED_GLASS_PANE }
	}
}