package net.wapic.wpcmod.features.dungeons.floor7.terminals

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class StartsWithTerminalScreen(menu: ChestMenu, title: Component) : AbstractTerminalScreen(menu, title) {
	override val gameWidth: Int = 7
	override val gameHeight: Int = 3

	private val letter = Terminal.STARTS_WITH_PATTERN.matchEntire(title.string)?.groupValues?.get(1)

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		for (slotIndex in solution) {
			extractSlot(graphics, slotIndex, config.startsWithColor)
		}
	}

	override fun slotClicked(slotIndex: Int, button: Int, input: ContainerInput): Boolean {
		if (slotIndex in solution) {
			solution.removeIf { it == slotIndex }
			doTerminalClick(slotIndex, InputConstants.MOUSE_BUTTON_MIDDLE, ContainerInput.CLONE)
			return true
		}
		return false
	}

	override fun solveTerminal(slots: List<Slot>): List<Int> {
		return slots.mapNotNull { slot -> slot.index.takeIf { hasLetter(slot.item) } }
	}

	private fun hasLetter(stack: ItemStack): Boolean {
		return letter?.let { stack.hoverName.string.startsWith(it, true) } == true
	}
}