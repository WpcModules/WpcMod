package net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.wapic.wpcmod.features.dungeons.floor7.terminals.RubixTerminalScreen
import net.wapic.wpcmod.features.dungeons.floor7.terminals.Terminal

class RubixTerminalSimulator(override val screen: RubixTerminalScreen) : AbstractTerminalSimulator(screen, Terminal.Type.RUBIX) {

	override fun create() {
		this.setSlots { slot ->
			if(slot.index %9 in 3..5 && slot.index / 9 in 1..3) {
				return@setSlots screen.rubixColorOrder.random().defaultInstance
			}
			return@setSlots blackPane
		}
	}

	override fun onClick(slotIndex: Int, button: Int) {
		this.setSlots { slot ->
			if(slot.index == slotIndex) {
				val delta = if(button == 0) 1 else -1
				val index = (screen.rubixColorOrder.indexOf(slot.item.item) + delta + screen.rubixColorOrder.size) % screen.rubixColorOrder.size
				return@setSlots screen.rubixColorOrder[index].defaultInstance
			}
			return@setSlots slot.item
		}
	}

	override fun onUpdate(items: Array<ItemStack?>) {
		if(items.filter { it?.item != Items.BLACK_STAINED_GLASS_PANE }.all {
			screen.rubixColorOrder.indexOf(it?.item) == screen.goal
		}) this.onSolve()
	}
}