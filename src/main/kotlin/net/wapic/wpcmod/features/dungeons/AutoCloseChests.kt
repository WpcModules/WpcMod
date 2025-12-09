package net.wapic.wpcmod.features.dungeons

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.sounds.SoundEvents
import net.minecraft.network.chat.Component
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.MC

object AutoCloseChests {

	private val config get() = WpcMod.config.dungeon
	private val defaultTitles = listOf("Chest", "Large Chest")

	fun init() {
		ScreenEvents.AFTER_INIT.register { _, screen, _, _ -> onScreenInit(screen) }
	}

	fun onScreenInit(screen: Screen) {
		if (!config.autoCloseChests || !DungeonUtils.inDungeons) return
		if (screen !is ContainerScreen || !defaultTitles.contains(screen.title.string)) return

		if (config.alertOnTreasureTalismans) {
			ScreenEvents.afterTick(screen).register { screen ->
				(screen as ContainerScreen).menu?.container?.find { stack ->
					stack.hoverName.string.contains(
						"Treasure Talisman"
					)
				}?.let { stack ->
					ChatUtils.sendAlert(Component.literal(stack.hoverName.string).setStyle(stack.hoverName.style))
					ChatUtils.sendMessage(stack.hoverName.string, stack.hoverName.style)
					MC.player?.makeSound(SoundEvents.EXPERIENCE_ORB_PICKUP)
				}
			}
		}

		screen.onClose()
	}
}