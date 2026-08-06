package net.wapic.wpcmod.features.dungeons.floor7.terminals

import com.mojang.blaze3d.platform.InputConstants
import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Items

class MelodyTerminalScreen(menu: ChestMenu, title: Component) : AbstractTerminalScreen(menu, title) {
	private var canClick = true

	override fun resize(width: Int, height: Int) {
		this.height = ((menu.rowCount + 1.5f) * totalSlotSpace).toInt()
		this.width = (font.width(title) * config.customTermSize * 1.25f).toInt()
	}

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		for (slot in menu.slots.subList(0, menu.container.containerSize)) {
			when(slot.item.item) {
				Items.RED_STAINED_GLASS_PANE, Items.RED_TERRACOTTA -> extractSlot(graphics, slot.index, config.melodyRowColor)
				Items.LIME_STAINED_GLASS_PANE, Items.LIME_TERRACOTTA -> extractSlot(graphics, slot.index, config.melodyPointerColor)
				Items.MAGENTA_STAINED_GLASS_PANE -> extractSlot(graphics, slot.index, config.melodyColumColor)
				Items.WHITE_STAINED_GLASS_PANE -> extractSlot(graphics, slot.index, ChromaColour(1f, 0.1f, 1f, 0, 255))
				else -> continue
			}
		}
	}

	override fun slotClicked(slotIndex: Int, button: Int): Boolean {
		if (!canClick) return false
		return doTerminalClick(slotIndex, InputConstants.MOUSE_BUTTON_MIDDLE)
	}

	override fun onUpdate(slots: List<Slot>) {
		val pointer = slots.indexOfFirst { it.item.item == Items.LIME_STAINED_GLASS_PANE }
		val column = slots.indexOfFirst { it.item.item == Items.MAGENTA_STAINED_GLASS_PANE }
		canClick = pointer % 9 == column % 9 && pointer != -1 && column != -1
	}
}