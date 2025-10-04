package net.wapic.wpcmod.features.inventory

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.item.Items
import net.minecraft.screen.slot.SlotActionType
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Utils
import org.lwjgl.glfw.GLFW

class ArmorSwapper {

	private val config get() = WpcMod.config.inventory

	private val armorSwapBind: KeyBinding =
		KeyBindingHelper.registerKeyBinding(KeyBinding("Armor Swap", InputUtil.GLFW_KEY_V, "WpcMod"))
	private val wardrobeTitle = "Wardrobe \\((?<page>[1-2])/2\\)".toRegex()
	private var sorrowPiece = "(?:\\w+\\s)?Sorrow Boots".toRegex()
	private var shouldSwap = false

	private var sorrowSlot: Int? = null
	private var lastArmorSlot: Int? = null

	init {
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
	}

	private fun onTick(client: MinecraftClient) {
		if (armorSwapBind.wasPressed() && config.armorSwapper) {
			Utils.addToCommandQueue("wardrobe")
			shouldSwap = true
		}

		if (!shouldSwap) return

		val screen = (client.currentScreen as? GenericContainerScreen) ?: return
		if (!screen.title.string.contains(wardrobeTitle)) return
		val inv = screen.screenHandler.inventory

		for (index in 27..<inv.size()) {
			val stack = inv.getStack(index)
			if (stack.item == Items.AIR) continue

			if (stack.name.string.matches(sorrowPiece)) {
				sorrowSlot = index + 9
			}

			if (stack.name.string.contains("Equipped".toRegex())) {
				if (sorrowSlot == index) {
					lastArmorSlot?.let {
						client.interactionManager?.clickSlot(
							screen.screenHandler.syncId,
							it,
							GLFW.GLFW_MOUSE_BUTTON_LEFT,
							SlotActionType.PICKUP,
							client.player
						)
					}

					screen.close()
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
			if (inv.getStack(it) == null) return
			if (!inv.getStack(it).name.string.contains("Ready")) return

			client.interactionManager?.clickSlot(
				screen.screenHandler.syncId, it, GLFW.GLFW_MOUSE_BUTTON_LEFT, SlotActionType.PICKUP, client.player
			)
			screen.close()
			shouldSwap = false
			sorrowSlot = null
		}
	}
}