package net.wapic.wpcmod.features.dungeons.floor7.terminals

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Items

class MelodyTerminalScreen(menu: ChestMenu, title: Component) : AbstractTerminalScreen(menu, title) {

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		for (slot in menu.slots) {
			when (slot.item.item) {
				Items.LIME_STAINED_GLASS_PANE, Items.LIME_TERRACOTTA -> extractSlot(
					graphics,
					slot.index,
					config.melodyPointerColor
				)
				Items.RED_STAINED_GLASS_PANE -> extractSlot(graphics, slot.index, config.melodyRowColor)
				Items.MAGENTA_STAINED_GLASS_PANE -> extractSlot(graphics, slot.index, config.melodyColumColor)
				else -> continue
			}
		}
	}

	override fun slotClicked(slotIndex: Int, button: Int, input: ContainerInput): Boolean {
		var pointer = -1
		var column = -1
		var slotToClick = -1
		for (slot in menu.slots.subList(0, menu.container.containerSize)) {
			if (slot.item.item == Items.LIME_STAINED_GLASS_PANE) pointer = slot.index
			if (slot.item.item == Items.MAGENTA_STAINED_GLASS_PANE) column = slot.index
			if (slot.item.item == Items.LIME_TERRACOTTA) slotToClick = slot.index
		}

		if (slotIndex == slotToClick && pointer != -1 && column != -1 && pointer % 9 == column % 9) {
			doTerminalClick(slotIndex, button, input)
			return true
		}
		return false
	}

	// Melody doesn't receive slot updates like other terminals for some reason
	override fun solveTerminal(slots: List<Slot>) = Unit
}