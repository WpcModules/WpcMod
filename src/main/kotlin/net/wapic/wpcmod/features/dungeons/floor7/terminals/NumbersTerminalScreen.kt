package net.wapic.wpcmod.features.dungeons.floor7.terminals

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Items
import org.lwjgl.glfw.GLFW

class NumbersTerminalScreen(menu: ChestMenu, title: Component) :
	AbstractTerminalScreen(TerminalType.NUMBERS, menu, title) {

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		items.filter { it?.item?.item == Items.RED_STAINED_GLASS_PANE }.sortedBy { it?.item?.count }
			.forEachIndexed { index, slot ->
				val color = when (index) {
					0 -> config.orderColor
					1 -> config.orderColor2
					2 -> config.orderColor3
					else -> config.backgroundColor
				}

				slot?.let { slot ->
					extractSlot(graphics, slot, color, if (config.showNumbers) slot.item.count.toString() else "")
				}
			}
	}

	override fun slotClicked(slot: Slot, button: Int): Boolean {
		if (slot.item.item != Items.RED_STAINED_GLASS_PANE) return false

		val nextSlot =
			items.filterNotNull().filter { it.item.item == Items.RED_STAINED_GLASS_PANE }.minByOrNull { it.item.count }
		if (slot.index != nextSlot?.index) return false

		doTerminalClick(slot, GLFW.GLFW_MOUSE_BUTTON_MIDDLE)
		return true
	}
}