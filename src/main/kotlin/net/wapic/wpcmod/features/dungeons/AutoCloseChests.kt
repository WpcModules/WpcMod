package net.wapic.wpcmod.features.dungeons

import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.contents.TranslatableContents
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.GuiEvents
import net.wapic.wpcmod.util.dungeons.DungeonUtils

object AutoCloseChests {

	private val config get() = WpcMod.config.dungeon
	private val defaultTitles = listOf("container.chest", "container.chestDouble")

	fun init() {
		GuiEvents.BEFORE_OPEN.register(::onScreenInit)
	}

	fun onScreenInit(screen: Screen) {
		if (!config.autoCloseChests || !DungeonUtils.inDungeons) return

		val title = (screen.title.contents as? TranslatableContents)?.key ?: return
		if (title in defaultTitles) {
			screen.onClose()
		}
	}
}