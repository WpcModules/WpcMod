package net.wapic.wpcmod.util

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.GameOptions
import net.minecraft.client.util.Window
import net.minecraft.client.world.ClientWorld
import net.minecraft.item.ItemStack
import net.minecraft.resource.ResourceManager

object MC {

	fun runOnThread(run: () -> Unit) = if(instance.isOnThread) run() else instance.send { run() }

	inline val instance: MinecraftClient get() = MinecraftClient.getInstance()
	inline val player: ClientPlayerEntity? get() = instance.player
	inline val textRenderer: TextRenderer get() = instance.textRenderer
	inline val world: ClientWorld? get() = instance.world
	inline val heldItem: ItemStack get() = player?.mainHandStack ?: ItemStack.EMPTY
	inline val window: Window get() = instance.window
	inline val inGameHud: InGameHud get() = instance.inGameHud
	inline val options: GameOptions get() = instance.options
	inline val networkHandler get() = player?.networkHandler
	inline var screen: Screen?
		set(value) = instance.setScreen(value)
		get() = instance.currentScreen
	inline val screenName: String? get() = screen?.title?.string
	inline val textureManager get() = instance.textureManager
	inline val resourceManager: ResourceManager get() = instance.resourceManager
	inline val interactionManager get() = instance.interactionManager
	inline val mouse get() = instance.mouse
}