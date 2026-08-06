package net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator

import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.ItemStack
import net.wapic.wpcmod.features.dungeons.floor7.terminals.StartsWithTerminalScreen
import net.wapic.wpcmod.features.dungeons.floor7.terminals.Terminal
import kotlin.random.Random

class StartsWithTerminalSimulator(override val screen: StartsWithTerminalScreen) : AbstractTerminalSimulator(screen, Terminal.Type.STARTS_WITH) {

	val wrongItems = BuiltInRegistries.ITEM.filterNot { screen.isValidItem(it.defaultInstance) }.map { it.defaultInstance }
	val correctItems = BuiltInRegistries.ITEM.filter { screen.isValidItem(it.defaultInstance) }.map { it.defaultInstance }

	override fun create() {
		this.setSlots { slot ->
			if(slot.index % 9 in 1..7 && slot.index / 9 in 1..4) {
				return@setSlots if(Random.nextDouble() > 0.65) correctItems.random() else wrongItems.random()
			}
			return@setSlots blackPane
		}
	}

	override fun onClick(slotIndex: Int, button: Int) {
		this.setSlots { slot ->
			if(slot.index == slotIndex) slot.item.copy().apply { set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true) } else slot.item
		}
	}

	override fun onUpdate(items: Array<ItemStack?>) {
		if(items.withIndex().none { screen.isValidItem(it.value, it.index) } ) this.onSolve()
	}
}