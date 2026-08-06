package net.wapic.wpcmod.features.dungeons.floor7.terminals

import com.mojang.blaze3d.platform.InputConstants
import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.wapic.wpcmod.WpcMod

class MelodyTerminalScreen(menu: ChestMenu, title: Component) :
	AbstractTerminalScreen(Terminal.Type.MELODY, menu, title) {
	private var disableClick = true

	override fun resize(width: Int, height: Int) {
		super.resize(width, height)
		this.height = ((menu.rowCount + 1.5f) * totalSlotSpace).toInt()
	}

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		items.forEachIndexed { slotIndex, stack ->
			when (stack?.item) {
				Items.RED_STAINED_GLASS_PANE, Items.RED_TERRACOTTA -> extractSlot(graphics, slotIndex, config.melodyRowColor)
				Items.LIME_STAINED_GLASS_PANE, Items.LIME_TERRACOTTA -> extractSlot(graphics, slotIndex, config.melodyPointerColor)
				Items.MAGENTA_STAINED_GLASS_PANE -> extractSlot(graphics, slotIndex, config.melodyColumColor)
				Items.WHITE_STAINED_GLASS_PANE -> extractSlot(graphics, slotIndex, ChromaColour(1f, 0.1f, 1f, 0, 255))
				else -> return@forEachIndexed
			}
		}
	}

	override fun slotClicked(slotIndex: Int, button: Int): Boolean {
		if (disableClick) return false
		return doTerminalClick(slotIndex, InputConstants.MOUSE_BUTTON_MIDDLE)
	}

	override fun onUpdate(items: Array<ItemStack?>) {
		val pointer = items.indexOfFirst { it?.item == Items.LIME_STAINED_GLASS_PANE }
		val column = items.indexOfFirst { it?.item == Items.MAGENTA_STAINED_GLASS_PANE }
		disableClick = pointer % 9 != column % 9 || pointer == -1 || column == -1
		WpcMod.LOGGER.debug("disableClick {}", disableClick)
	}
}