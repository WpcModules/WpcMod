package net.wapic.wpcmod.features.dungeons.floor7.terminals

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot

class StartsWithTerminalScreen(menu: ChestMenu, title: Component) :
	AbstractTerminalScreen(menu, title) {

	private val clickedSlots = mutableSetOf<Int>()
	val letter = Terminal.STARTS_WITH_PATTERN.matchEntire(title.string)?.groupValues?.get(1)

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		for (slotIndex in solution) {
			extractSlot(graphics, slotIndex, config.startsWithColor)
		}
	}

	override fun slotClicked(slotIndex: Int, button: Int, input: ContainerInput): Boolean {
		if (slotIndex in solution) {
			clickedSlots.add(slotIndex)
			solution.removeIf { it == slotIndex }
			doTerminalClick(slotIndex, InputConstants.MOUSE_BUTTON_MIDDLE, ContainerInput.CLONE)
			return true
		}
		return false
	}

	override fun solveTerminal(slots: List<Slot>) {
		solution.addAll(slots.mapNotNull { slot -> slot.index.takeIf { hasLetterAndNotClicked(slot) } })
	}

	fun hasLetterAndNotClicked(slot: Slot): Boolean {
		if (slot.index in clickedSlots) return false

		if (slot.item.hasFoil() && !slot.item.item.defaultInstance.hasFoil()) return false
		return letter?.let { slot.item.hoverName.string.startsWith(it, true) } == true
	}
}