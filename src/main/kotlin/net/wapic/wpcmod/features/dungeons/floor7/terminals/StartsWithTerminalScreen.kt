package net.wapic.wpcmod.features.dungeons.floor7.terminals

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.item.ItemStack
import net.wapic.wpcmod.WpcMod

class StartsWithTerminalScreen(menu: ChestMenu, title: Component) :
	AbstractTerminalScreen(Terminal.Type.STARTS_WITH, menu, title) {
	private val startsWithRegex = Regex("^What starts with: '(\\w)'\\?$")
	private val solution = mutableListOf<Int>()
	private val clickedSlots = mutableListOf<Int>()
	val letter = startsWithRegex.matchEntire(title.string)?.groupValues?.get(1)
	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		for (slotIndex in solution) {
			extractSlot(graphics, slotIndex, config.startsWithColor)
		}
	}

	override fun slotClicked(slotIndex: Int, button: Int): Boolean {
		if (slotIndex !in solution) return false
		if (doTerminalClick(slotIndex, InputConstants.MOUSE_BUTTON_MIDDLE)) {
			clickedSlots.add(slotIndex)
			return solution.removeIf { it == slotIndex }
		}
		return false
	}

	override fun onUpdate(items: Array<ItemStack?>) {
		solution.addAll(items.mapIndexedNotNull { index, stack ->  if(isValidItem(stack, index)) index else null })
	}

	fun isValidItem(stack: ItemStack?, slotIndex: Int = -1): Boolean {
		if (slotIndex in clickedSlots) return false

		if (stack?.hasFoil() == true && !stack.item.defaultInstance.hasFoil()) return false
		return letter?.let { stack?.hoverName?.string?.startsWith(it) } == true
	}
}