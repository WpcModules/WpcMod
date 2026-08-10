package net.wapic.wpcmod.util

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.MenuType
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.dungeons.floor7.terminals.AbstractTerminalScreen
import net.wapic.wpcmod.features.dungeons.floor7.terminals.Terminal

// Modified from SkyHanni
object MenuScreenHook {

	@JvmStatic
	fun <T : AbstractContainerMenu> openCustomMenu(
		title: Component,
		type: MenuType<T>,
		client: Minecraft,
		containerId: Int
	): Boolean {
		val player = client.player ?: return false
		if (!Terminal.shouldReplace(title)) return false

		val inventory = player.inventory
		val menu = type.create(containerId, inventory) as? ChestMenu ?: return false

		player.containerMenu = menu
		WpcMod.LOGGER.debug("Set menu to: {}, currentScreen {}", player.containerMenu, client.screen)

		when (val screen = client.screen) {
			is AbstractTerminalScreen -> {
				screen.changeHandler(menu)
				WpcMod.LOGGER.debug("Changed Menu Handler to {}, with ID: {}", menu, menu.containerId)
			}

			else ->  {
				client.setScreen(Terminal.createSolverScreen(menu, title))
				WpcMod.LOGGER.debug("Opened custom menu {}, currentScreen: {}", player.containerMenu, client.screen)
			}
		}

		return true
	}
}