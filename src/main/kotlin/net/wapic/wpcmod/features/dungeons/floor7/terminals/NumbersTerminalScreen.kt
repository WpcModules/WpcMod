package net.wapic.wpcmod.features.dungeons.floor7.terminals

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Items

class NumbersTerminalScreen(menu: ChestMenu, title: Component) : AbstractTerminalScreen(menu, title) {

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		solution.forEachIndexed { index, slotIndex ->
			val color = when (index) {
				0 -> config.orderColor
				1 -> config.orderColor2
				2 -> config.orderColor3
				else -> return@forEachIndexed
			}

			extractSlot(
				graphics,
				slotIndex,
				color,
				if (config.showNumbers) menu.getSlot(slotIndex).item.count.toString() else ""
			)
		}
	}

	override fun slotClicked(slotIndex: Int, button: Int, input: ContainerInput): Boolean {
		if (slotIndex == solution.firstOrNull()) {
			solution.removeIf { it == slotIndex }
			doTerminalClick(slotIndex, InputConstants.MOUSE_BUTTON_MIDDLE, ContainerInput.CLONE)
			return true
		}
		return false
	}

	override fun solveTerminal(slots: List<Slot>) {
		solution.addAll(
			slots.sortedBy { it.item.count }.mapNotNull { slot ->
				slot.index.takeIf { slot.item.item == Items.RED_STAINED_GLASS_PANE }
			}
		)
	}
}