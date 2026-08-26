package net.wapic.wpcmod.features.dungeons.floor7.terminals

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Items

class PanesTerminalScreen(menu: ChestMenu, title: Component) : AbstractTerminalScreen(menu, title) {

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		for (slot in solution) {
			extractSlot(graphics, slot, config.panesColor)
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

	override fun solveTerminal(slots: List<Slot>) {
		solution.addAll(slots.mapNotNull { slot -> slot.index.takeIf { slot.item.item == Items.RED_STAINED_GLASS_PANE } })
	}
}