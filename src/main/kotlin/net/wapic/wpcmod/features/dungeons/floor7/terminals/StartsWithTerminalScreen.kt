package net.wapic.wpcmod.features.dungeons.floor7.terminals

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.Slot
import org.lwjgl.glfw.GLFW

class StartsWithTerminalScreen(menu: ChestMenu, title: Component) :
	AbstractTerminalScreen(TerminalType.STARTS_WITH, menu, title) {
	private val startsWithRegex = Regex("^What starts with: '(\\w)'\\?$")
	private val letter = startsWithRegex.find(title.string)?.groupValues?.get(1)

	// We need to keep track of clicked slots cause items like Nether star has foil by default
	private val clickedSlots = mutableListOf<Int>()

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		for (slot in items.filterNotNull()) {
			if (!isClickableItem(slot)) {
				if (config.debug) extractSlot(graphics, slot, ChromaColour(0f, 0f, 0f, 0, 0))
				continue
			}
			extractSlot(graphics, slot, config.selectColor)
		}
	}

	override fun slotClicked(slot: Slot, button: Int): Boolean {
		if (!isClickableItem(slot)) return false

		if (doTerminalClick(slot, GLFW.GLFW_MOUSE_BUTTON_MIDDLE)) {
			return clickedSlots.add(slot.index)
		}
		return false
	}

	private fun isClickableItem(slot: Slot): Boolean {
		if (slot.index in clickedSlots) return false

		if (slot.item.hasFoil() && !slot.item.item.defaultInstance.hasFoil()) return false

		letter?.let {
			if (slot.item.hoverName.string.startsWith(it)) return true
		}

		return false
	}
}