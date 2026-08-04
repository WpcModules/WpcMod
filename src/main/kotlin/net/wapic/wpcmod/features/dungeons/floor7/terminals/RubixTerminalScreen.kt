package net.wapic.wpcmod.features.dungeons.floor7.terminals

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.DyeColor

class RubixTerminalScreen(menu: ChestMenu, title: Component) : AbstractTerminalScreen(TerminalType.RUBIX, menu, title) {
	private val rubixColorOrder = listOf(DyeColor.ORANGE, DyeColor.YELLOW, DyeColor.GREEN, DyeColor.BLUE, DyeColor.RED)

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		for (slot in items.filterNotNull()) {
			extractSlot(graphics, slot, config.backgroundColor)
		}
	}

	override fun slotClicked(slot: Slot, button: Int): Boolean {
		doTerminalClick(slot, button)
		return true
	}
}