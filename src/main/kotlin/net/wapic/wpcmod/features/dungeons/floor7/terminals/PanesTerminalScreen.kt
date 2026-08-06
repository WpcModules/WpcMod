package net.wapic.wpcmod.features.dungeons.floor7.terminals

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Items

class PanesTerminalScreen(menu: ChestMenu, title: Component) : AbstractTerminalScreen(menu, title) {

	override fun resize(width: Int, height: Int) {
		this.height = ((menu.rowCount + 0.5f) * totalSlotSpace).toInt()
		this.width = (font.width(title) * config.customTermSize * 1.25f).toInt()
	}

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		for (slotIndex in solution) {
			extractSlot(graphics, slotIndex, config.panesColor)
		}
	}

	override fun slotClicked(slotIndex: Int, button: Int): Boolean {
		if (slotIndex !in solution) return false
		if (doTerminalClick(slotIndex, InputConstants.MOUSE_BUTTON_MIDDLE)) {
			return solution.removeIf { slotIndex == it }
		}
		return false
	}

	override fun onUpdate(slots: List<Slot>) {
		solution.addAll(slots.mapNotNull { slot ->
			slot.index.takeIf { slot.item.item == Items.RED_STAINED_GLASS_PANE }
		})
	}
}