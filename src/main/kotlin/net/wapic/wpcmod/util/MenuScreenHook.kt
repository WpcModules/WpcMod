package net.wapic.wpcmod.util

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.MenuType
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.dungeons.floor7.terminals.*

// Modified from SkyHanni
object MenuScreenHook {

	@JvmStatic
	fun <T : AbstractContainerMenu> openCustomMenu(
		title: Component,
		type: MenuType<T>,
		mc: Minecraft,
		containerId: Int
	): Boolean {
		val player = mc.player ?: return false

		val inventory = player.inventory
		val menu = type.create(containerId, inventory)

		if (menu is ChestMenu && Terminal.shouldReplace(title)) {
			player.containerMenu = menu

			when (val screen = MC.screen) {
				is PanesTerminalScreen, is MelodyTerminalScreen, is SelectAllTerminalScreen,
				is RubixTerminalScreen, is NumbersTerminalScreen, is StartsWithTerminalScreen -> screen.changeHandler(
					menu
				)
				else -> MC.screen = Terminal.createScreen(menu, title)
			}
			return true
		}

		return false
	}
}