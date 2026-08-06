package net.wapic.wpcmod.features.dungeons.floor7.terminals

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class PanesTerminalScreen(menu: ChestMenu, title: Component) : AbstractTerminalScreen(Terminal.Type.PANES, menu, title) {

	private val solution = mutableListOf<Int>()

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		for (slotIndex in solution) {
			extractSlot(graphics, slotIndex, config.panesColor)
		}
	}

	override fun slotClicked(slotIndex: Int, button: Int): Boolean {
		if (slotIndex !in solution) return false
		if (doTerminalClick(slotIndex, InputConstants.MOUSE_BUTTON_MIDDLE)) {
			return solution.removeIf { slotIndex == it }
		}
		return false
	}

	override fun onUpdate(items: Array<ItemStack?>) {
		solution.addAll(items.mapIndexedNotNull { slotIndex, stack ->
			if(isValidItem(stack)) slotIndex else null
		})
	}

	fun isValidItem(stack: ItemStack?): Boolean = stack?.item == Items.RED_STAINED_GLASS_PANE
}