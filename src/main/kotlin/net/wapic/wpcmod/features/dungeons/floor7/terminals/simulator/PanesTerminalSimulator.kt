package net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.Items
import net.wapic.wpcmod.features.dungeons.floor7.terminals.PanesTerminalScreen
import net.wapic.wpcmod.features.dungeons.floor7.terminals.Terminal
import kotlin.random.Random

class PanesTerminalSimulator(override val screen: PanesTerminalScreen) : AbstractTerminalSimulator(screen, Terminal.Type.PANES) {
	private val redPane = ItemStackTemplate(Items.RED_STAINED_GLASS_PANE, emptyNameData)
	private val limePane = ItemStackTemplate(Items.LIME_STAINED_GLASS_PANE, emptyNameData)

	override fun create() {
		this.setSlots { slot ->
			if(slot.index % 9 in 2..6 && slot.index / 9 in 1..3) {
				return@setSlots if(Random.nextDouble() < 0.65) redPane.create() else limePane.create()
			}
			blackPane
		}
	}

	override fun onClick(slotIndex: Int, button: Int) {
		this.setSlots { slot ->
			if(slot.index != slotIndex) slot.item else limePane.create()
		}
	}

	override fun onUpdate(items: Array<ItemStack?>) {
		if(items.none(screen::isValidItem)) this.onSolve()
	}
}