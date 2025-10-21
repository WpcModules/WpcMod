package net.wapic.wpcmod.features.dungeons

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.MC

class AutoCloseChests {

	private val config get() = WpcMod.config.dungeon
	private val defaultTitles = listOf("Chest", "Large Chest")

	init {
		ScreenEvents.AFTER_INIT.register { _, screen, _, _ -> onScreenInit(screen) }
	}

	fun onScreenInit(screen: Screen) {
		if (!config.autoCloseChests || !DungeonUtils.inDungeons) return
		if (screen !is GenericContainerScreen || !defaultTitles.contains(screen.title.string)) return

		if (config.alertOnTreasureTalismans) {
			ScreenEvents.afterTick(screen).register { screen ->
				(screen as GenericContainerScreen).screenHandler?.inventory?.find { stack ->
					stack.name.string.contains(
						"Treasure Talisman"
					)
				}?.let { stack ->
					ChatUtils.sendAlert(Text.literal(stack.name.string).setStyle(stack.name.style))
					ChatUtils.sendMessage(stack.name.string, stack.name.style)
					MC.player?.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP)
				}
			}
		}

		screen.close()
	}
}