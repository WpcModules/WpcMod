package net.wapic.wpcmod.features.dungeons

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils

class AutoCloseChests {

	private val config get() = WpcMod.config.dungeon
	private val defaultTitles = listOf("Chest", "Large Chest")

	init {
		ScreenEvents.AFTER_INIT.register { _, screen, _, _ -> onScreenInit(screen) }
	}

	fun onScreenInit(screen: Screen) {
		if (!config.autoCloseChests || Utils.getLocation() != Island.DUNGEON) return
		if (screen !is GenericContainerScreen || !defaultTitles.contains(screen.title.string)) return

		if (config.alertOnTreasureTalismans) {
			ScreenEvents.afterTick(screen).register { screen ->
				(screen as GenericContainerScreen).screenHandler?.inventory?.find { stack ->
					stack.name.string.contains(
						"Treasure Talisman"
					)
				}?.let { stack ->
					ChatUtils.sendAlert(stack.name.string, stack.name.style)
				}
				screen.close()
			}
		} else {
			screen.close()
		}
	}
}