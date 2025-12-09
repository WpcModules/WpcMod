package net.wapic.wpcmod.features.inventory

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.client.KeyMapping
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.world.item.Items
import net.minecraft.world.inventory.ClickType
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Utils
import org.lwjgl.glfw.GLFW

object ArmorSwapper {

	private val config get() = WpcMod.config.inventory

	private val armorSwapBind: KeyMapping =
		KeyBindingHelper.registerKeyBinding(KeyMapping("Armor Swap", InputConstants.KEY_V, WpcMod.category))
	private val wardrobeTitle = Regex("Wardrobe \\((?<page>\\d)/\\d\\)")
	private var sorrowPiece = Regex("(?:Ancient|Renowned) Sorrow Boots")
	private var shouldSwap = false

	private var sorrowSlot: Int? = null
	private var lastArmorSlot: Int? = null

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
	}

	private fun onTick(client: Minecraft) {
		if (armorSwapBind.consumeClick() && config.armorSwapper) {
			Utils.addToCommandQueue("wardrobe")
			shouldSwap = true
		}

		if (!shouldSwap) return

		val screen = (client.screen as? ContainerScreen) ?: return
		if (!screen.title.string.contains(wardrobeTitle)) return
		val inv = screen.menu.container

		for (index in 27..<inv.containerSize) {
			val stack = inv.getItem(index)
			if (stack.item == Items.AIR) continue

			if (stack.hoverName.string.matches(sorrowPiece)) {
				sorrowSlot = index + 9
			}

			if (stack.hoverName.string.contains("Equipped")) {
				if (sorrowSlot == index) {
					lastArmorSlot?.let {
						client.gameMode?.handleInventoryMouseClick(
							screen.menu.containerId,
							it,
							GLFW.GLFW_MOUSE_BUTTON_LEFT,
							ClickType.PICKUP,
							client.player
						)
					}

					screen.onClose()
					shouldSwap = false
					sorrowSlot = null
					lastArmorSlot = null
				} else {
					lastArmorSlot = index
					WpcMod.logger.info("set lastArmorSlot: $lastArmorSlot")
				}
			}
		}

		sorrowSlot?.let {
			if (inv.getItem(it) == null) return
			if (!inv.getItem(it).hoverName.string.contains("Ready")) return

			client.gameMode?.handleInventoryMouseClick(
				screen.menu.containerId, it, GLFW.GLFW_MOUSE_BUTTON_LEFT, ClickType.PICKUP, client.player
			)
			screen.onClose()
			shouldSwap = false
			sorrowSlot = null
		}
	}
}