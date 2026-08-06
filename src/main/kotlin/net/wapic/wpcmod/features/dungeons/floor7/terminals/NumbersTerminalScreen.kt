package net.wapic.wpcmod.features.dungeons.floor7.terminals

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Items

class NumbersTerminalScreen(menu: ChestMenu, title: Component) : AbstractTerminalScreen(menu, title) {

	override fun resize(width: Int, height: Int) {
		this.height = (menu.rowCount * totalSlotSpace).toInt()
		this.width = (((menu.container.containerSize - 1) % 9) * totalSlotSpace).toInt()
	}

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		solution.forEachIndexed { index, slotIndex ->

			val color = when (index) {
				0 -> config.orderColor
				1 -> config.orderColor2
				2 -> config.orderColor3
				else -> config.backgroundColor
			}

			extractSlot(graphics, slotIndex, color)
		}
	}

	override fun slotClicked(slotIndex: Int, button: Int): Boolean {
		if(slotIndex == solution.first()) {
			if(doTerminalClick(slotIndex, InputConstants.MOUSE_BUTTON_MIDDLE)) {
				solution.removeIf { it == slotIndex }
				return true
			}
		}
		return false
	}

	override fun onUpdate(slots: List<Slot>) {
		solution.addAll(slots.mapNotNull { slot ->
			(slot.item.count to slot.index).takeIf { slot.item.item == Items.RED_STAINED_GLASS_PANE }
		}.sortedBy { it.first }.map { it.second })
	}
}