package net.wapic.wpcmod.features.dungeons.floor7.terminals

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Items
import org.lwjgl.glfw.GLFW

class PanesTerminalScreen(menu: ChestMenu, title: Component) : AbstractTerminalScreen(TerminalType.PANES, menu, title) {

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		for (slot in items.filterNotNull()) {
			if (slot.item.item != Items.RED_STAINED_GLASS_PANE) continue
			extractSlot(graphics, slot, config.panesColor)
		}
	}

	override fun slotClicked(slot: Slot, button: Int): Boolean {
		if (slot.item.item != Items.RED_STAINED_GLASS_PANE) return false
		return doTerminalClick(slot, GLFW.GLFW_MOUSE_BUTTON_MIDDLE)
	}
}