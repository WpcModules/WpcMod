package net.wapic.wpcmod.features.dungeons

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ItemStack
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.EntityEvents
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.HeadTextures
import net.wapic.wpcmod.util.ItemUtils.headTexture
import net.wapic.wpcmod.util.MC

object AutoCloseChests {

	private val config get() = WpcMod.config.dungeon
	private val defaultTitles = listOf("container.chest", "container.chestDouble")

	fun init() {
		ScreenEvents.AFTER_INIT.register { _, screen, _, _ -> onScreenInit(screen) }
		EntityEvents.ITEM_DATA_SET.register(::onItemDataSet)
	}

	fun onItemDataSet(stack: ItemStack) {
		if (!config.alertOnTreasureTalismans || !DungeonUtils.inDungeons) return
		if (stack.headTexture == HeadTextures.TREASURE_TALISMAN) {
			ChatUtils.sendAlert(stack.hoverName)
			ChatUtils.sendMessage(stack.hoverName.string, stack.hoverName.style)
			MC.player?.makeSound(SoundEvents.EXPERIENCE_ORB_PICKUP)
		}
	}

	fun onScreenInit(screen: Screen) {
		if (!config.autoCloseChests || !DungeonUtils.inDungeons) return

		val title = (screen.title.contents as? TranslatableContents)?.key ?: return
		if (title in defaultTitles && screen is ContainerScreen) {
			screen.onClose()
		}
	}
}