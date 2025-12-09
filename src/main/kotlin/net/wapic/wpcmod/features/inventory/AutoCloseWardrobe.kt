package net.wapic.wpcmod.features.inventory

import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.world.item.Items
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.GuiEvents
import net.wapic.wpcmod.util.MC

object AutoCloseWardrobe {
	private val config get() = WpcMod.config.inventory

	private val wardrobeTitle = Regex("Wardrobe \\((?<page>\\d)/\\d\\)")
	private val slotEquippedRegex = Regex("^Slot \\d: Equipped$")

	fun init() {
		GuiEvents.SLOT_UPDATE_BEFORE.register { syncId, slotId, itemStack ->
			if(!config.autoCloseWardrobe) return@register
			val screen = (MC.screen as? ContainerScreen) ?: return@register
			val oldItem = screen.menu.container.getItem(slotId)

			if(oldItem.item == Items.AIR && itemStack.hoverName.string.contains(slotEquippedRegex)) {
				MC.runOnThread { MC.screen?.onClose() }
			}
		}
	}
}