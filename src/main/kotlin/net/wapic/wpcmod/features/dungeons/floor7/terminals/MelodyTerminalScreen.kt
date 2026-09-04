package net.wapic.wpcmod.features.dungeons.floor7.terminals

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Items
import net.wapic.wpcmod.WpcMod

class MelodyTerminalScreen(menu: ChestMenu, title: Component) : AbstractTerminalScreen(menu, title) {

	override val gameWidth: Int = 7
	override val gameHeight: Int = 5
	private val melodyMessageConfig get() = WpcMod.config.dungeon.floor7.melodyMessage

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		for (slot in slots) {
			when (slot.item.item) {
				Items.STAINED_GLASS_PANE.lime, Items.DYED_TERRACOTTA.lime -> extractSlot(
					graphics,
					slot.index,
					config.melodyPointerColor
				)
				Items.STAINED_GLASS_PANE.red -> extractSlot(graphics, slot.index, config.melodyRowColor)
				Items.STAINED_GLASS_PANE.magenta -> extractSlot(graphics, slot.index, config.melodyColumColor)
				else -> continue
			}
		}
	}

	override fun slotClicked(slotIndex: Int, button: Int, input: ContainerInput): Boolean {
		var pointer = -1
		var column = -1
		var slotToClick = -1
		for (slot in slots) {
			if (slot.item.item == Items.STAINED_GLASS_PANE.lime) pointer = slot.index
			if (slot.item.item == Items.STAINED_GLASS_PANE.magenta) column = slot.index
			if (slot.item.item == Items.DYED_TERRACOTTA.lime) slotToClick = slot.index
		}

		if (slotIndex == slotToClick && pointer != -1 && column != -1 && pointer % 9 == column % 9) {
			doTerminalClick(slotIndex, button, input)
			return true
		}
		return false
	}

	// Melody doesn't receive slot updates like other terminals for some reason
	override fun solveTerminal(slots: List<Slot>): List<Int> = emptyList()
}