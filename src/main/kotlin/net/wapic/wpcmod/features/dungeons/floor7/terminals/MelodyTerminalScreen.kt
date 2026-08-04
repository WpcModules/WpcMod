package net.wapic.wpcmod.features.dungeons.floor7.terminals

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Items
import org.lwjgl.glfw.GLFW

class MelodyTerminalScreen(menu: ChestMenu, title: Component) :
	AbstractTerminalScreen(TerminalType.MELODY, menu, title) {

	override fun init() {
		super.init()
		this.height = ((menu.rowCount + 1.5f) * totalSlotSpace).toInt()
	}

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		for (slot in items.filterNotNull()) {
			when (slot.item.item) {
				Items.RED_STAINED_GLASS_PANE, Items.RED_TERRACOTTA -> extractSlot(graphics, slot, config.melodyRowColor)
				Items.LIME_STAINED_GLASS_PANE, Items.LIME_TERRACOTTA -> extractSlot(
					graphics,
					slot,
					config.melodyPointerColor
				)
				Items.MAGENTA_STAINED_GLASS_PANE -> extractSlot(graphics, slot, config.melodyColumColor)
				Items.WHITE_STAINED_GLASS_PANE -> extractSlot(graphics, slot, ChromaColour(1f, 0.1f, 1f, 0, 255))
			}
		}
	}

	override fun slotClicked(slot: Slot, button: Int): Boolean {
		if (slot.item.item != Items.LIME_TERRACOTTA) return false

		val pointer = items.find { it?.item?.item == Items.LIME_STAINED_GLASS_PANE } ?: return false
		val column = items.find { it?.item?.item == Items.MAGENTA_STAINED_GLASS_PANE } ?: return false
		if (pointer.index % 9 != column.index % 9) return false

		doTerminalClick(slot, GLFW.GLFW_MOUSE_BUTTON_MIDDLE)
		return false
	}
}