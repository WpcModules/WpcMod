package net.wapic.wpcmod.features.dungeons.floor7.terminals

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class NumbersTerminalScreen(menu: ChestMenu, title: Component) :
	AbstractTerminalScreen(Terminal.Type.NUMBERS, menu, title) {

	private val solution = arrayOfNulls<Int>(14)
	private val sol = mutableSetOf<Int>()

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		val firstIndex = solution.indexOfFirst { it != null }

		solution.forEachIndexed { index, slotIndex ->
			if(slotIndex == null) return@forEachIndexed

			val color = when (index) {
				firstIndex + 0 -> config.orderColor
				firstIndex + 1 -> config.orderColor2
				firstIndex + 2 -> config.orderColor3
				else -> config.backgroundColor
			}

			extractSlot(graphics, slotIndex, color, if (config.showNumbers) (index + 1).toString() else "")
		}
	}

	override fun slotClicked(slotIndex: Int, button: Int): Boolean {
		val firstIndex = solution.indexOfFirst { it != null }
		if(slotIndex == solution[firstIndex]) {
			if(doTerminalClick(slotIndex, InputConstants.MOUSE_BUTTON_MIDDLE)) {
				solution[firstIndex] = null
				return true
			}
		}
		return false
	}

	override fun onUpdate(items: Array<ItemStack?>) {
		items.forEachIndexed { slotIndex, stack ->
			if(isValidItem(stack ?: return@forEachIndexed)) solution[stack.count - 1] = slotIndex
		}
	}

	fun isValidItem(stack: ItemStack?): Boolean = stack?.item == Items.RED_STAINED_GLASS_PANE
}