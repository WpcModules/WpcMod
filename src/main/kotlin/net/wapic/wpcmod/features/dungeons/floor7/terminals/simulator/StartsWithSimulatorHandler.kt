package net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.wapic.wpcmod.features.dungeons.floor7.terminals.Terminal
import kotlin.random.Random

class StartsWithSimulatorHandler(menu: ChestMenu, title: Component) : TerminalSimulatorHandler(menu) {

	private val letter = Terminal.STARTS_WITH_PATTERN.matchEntire(title.string)?.groupValues?.get(1)
	private val allItems = BuiltInRegistries.ITEM.map { it.defaultInstance }
	private val clickedSlots = mutableSetOf<Int>()

	override fun create() {
		this.setSlots { slot ->
			if(slot.index % 9 in 1..7 && slot.index / 9 in 1..4) {
				val correct = allItems.filter { isValidItem(it) }
				val incorrect = allItems.filterNot { isValidItem(it) }
				return@setSlots if(Random.nextBoolean()) correct.random().copy() else incorrect.random().copy()
			}
			return@setSlots blackPane
		}
	}

	override fun slotClicked(slot: Slot, slotId: Int, buttonNum: Int, containerInput: ContainerInput) {
		if(isValidItem(slot.item, slot.index)) {
			slot.item.applyComponents(glintOverrideData)
			slot.setItem(slot.item)
			clickedSlots.add(slot.index)
		}
	}

	override fun onUpdate(slots: List<Slot>) {
		if(slots.none { isValidItem(it.item, it.index) }) this.onSolve()
	}

	private fun isValidItem(stack: ItemStack?, slotIndex: Int = -1): Boolean {
		if (slotIndex in clickedSlots) return false
		if (stack?.hasFoil() == true && !stack.item.defaultInstance.hasFoil()) return false
		return stack?.hoverName?.string?.startsWith(letter ?: return false) == true
	}
}