package net.wapic.wpcmod.features.inventory

import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.world.inventory.ClickType
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.ItemUtils.skyblockId
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils
import org.lwjgl.glfw.GLFW

object ArmorSwapper {

	private val config get() = WpcMod.config.inventory

	private val armorSwapBind: KeyMapping =
		KeyBindingHelper.registerKeyBinding(KeyMapping("Armor Swap", InputConstants.KEY_V, WpcMod.category))
	private val wardrobeTitle = Regex("Wardrobe \\(\\d/\\d\\)")
	private const val SORROW_SKYBLOCK_ID = "SORROW_BOOTS"
	private var shouldSwap = false

	private var lastArmorSlot: Int? = null

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
	}

	private fun clickSlot(screen: ContainerScreen, slot: Int) {
		MC.gameMode?.handleInventoryMouseClick(
			screen.menu.containerId,
			slot,
			GLFW.GLFW_MOUSE_BUTTON_LEFT,
			ClickType.PICKUP,
			MC.player ?: return
		)
	}

	private fun onTick(client: Minecraft) {
		if (armorSwapBind.consumeClick() && config.armorSwapper) {
			WpcMod.LOGGER.debug("Armor swap triggered")
			Utils.addToCommandQueue("wardrobe")
			shouldSwap = true
		}

		if (!shouldSwap) return

		val screen = (client.screen as? ContainerScreen) ?: return
		if (!screen.title.string.matches(wardrobeTitle)) return
		val inv = screen.menu.container

		lastArmorSlot?.let {
			if (inv.getItem(it).hoverName.string.contains("Ready")) {
				WpcMod.LOGGER.debug("equipping last armor")
				clickSlot(screen, it)
				screen.onClose()
				lastArmorSlot = null
				shouldSwap = false
			}
			return
		}

		val equippedArmorSlot = inv.indexOfFirst { it.hoverName.string.contains("Equipped") }
		if (equippedArmorSlot == -1) return
		WpcMod.LOGGER.debug("found equipped armor: $equippedArmorSlot")

		val sorrowSlot = inv.indexOfFirst { it.skyblockId == SORROW_SKYBLOCK_ID }
		if (sorrowSlot == -1) return
		WpcMod.LOGGER.debug("found sorrow armor: $sorrowSlot")

		if (inv.getItem(sorrowSlot + 9).hoverName.string.contains("Ready")) {
			WpcMod.LOGGER.debug("equipping sorrow")
			lastArmorSlot = equippedArmorSlot
			clickSlot(screen, sorrowSlot + 9)
			screen.onClose()
			shouldSwap = false
		}
	}
}