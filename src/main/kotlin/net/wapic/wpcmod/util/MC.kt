package net.wapic.wpcmod.util

import net.minecraft.client.KeyboardHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.MouseHandler
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.Options
import net.minecraft.client.renderer.texture.TextureManager
import com.mojang.blaze3d.platform.Window
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.world.item.ItemStack
import net.minecraft.server.packs.resources.ResourceManager

object MC {

	fun runOnThread(run: () -> Unit) = if(instance.isSameThread) run() else instance.schedule { run() }

	inline val instance: Minecraft get() = Minecraft.getInstance()
	inline val player: LocalPlayer? get() = instance.player
	inline val textRenderer: Font get() = instance.font
	inline val world: ClientLevel? get() = instance.level
	inline val heldItem: ItemStack get() = player?.mainHandItem ?: ItemStack.EMPTY
	inline val window: Window get() = instance.window
	inline val inGameHud: Gui get() = instance.gui
	inline val options: Options get() = instance.options
	inline val networkHandler get() = player?.connection
	inline var screen: Screen?
		set(value) = instance.setScreen(value)
		get() = instance.screen
	inline val screenName: String? get() = screen?.title?.string
	inline val textureManager: TextureManager get() = instance.textureManager
	inline val resourceManager: ResourceManager get() = instance.resourceManager
	inline val interactionManager get() = instance.gameMode
	inline val mouse: MouseHandler get() = instance.mouseHandler
	inline val keyboard: KeyboardHandler get() = instance.keyboardHandler
}