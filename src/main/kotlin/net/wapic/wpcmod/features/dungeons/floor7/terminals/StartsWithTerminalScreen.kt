package net.wapic.wpcmod.features.dungeons.floor7.terminals

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.Slot

class StartsWithTerminalScreen(menu: ChestMenu, title: Component) :
	AbstractTerminalScreen(menu, title) {

	private val clickedSlots = mutableSetOf<Int>()
	val letter = Terminal.STARTS_WITH_PATTERN.matchEntire(title.string)?.groupValues?.get(1)

	override fun resize(width: Int, height: Int) {
		this.height = ((menu.rowCount + 0.5f) * totalSlotSpace).toInt()
		this.width = (font.width(title) * config.customTermSize * 1.25f).toInt()
	}

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

	override fun onInventoryUpdated(slots: List<Slot>) {
		solution.addAll(slots.mapNotNull{ slot -> slot.index.takeIf { hasLetterAndNotClicked(slot) } })
	}

	fun hasLetterAndNotClicked(slot: Slot): Boolean {
		if (slot.index in clickedSlots) return false

		if (slot.item.hasFoil() && !slot.item.item.defaultInstance.hasFoil()) return false
		return letter?.let { slot.item.hoverName.string.startsWith(it, true) } == true
	}
}