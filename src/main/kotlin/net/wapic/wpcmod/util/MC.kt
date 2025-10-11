package net.wapic.wpcmod.util

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.util.Window
import net.minecraft.client.world.ClientWorld
import net.minecraft.item.ItemStack
import net.wapic.wpcmod.mixin.accessors.MinecraftClientAccessor

object MC {

	fun runOnThread(run: () -> Unit) = if(instance.isOnThread) run() else instance.send { run() }

	inline val instance: MinecraftClient get() = MinecraftClient.getInstance()
	inline val accessor: MinecraftClientAccessor get() = instance as MinecraftClientAccessor
	inline val player: ClientPlayerEntity? get() = instance.player
	inline val textRenderer: TextRenderer get() = instance.textRenderer
	inline val world: ClientWorld? get() = instance.world
	inline val heldItem: ItemStack get() = player?.mainHandStack ?: ItemStack.EMPTY
	inline val window: Window get() = instance.window
	inline val inGameHud get() = instance.inGameHud
	inline val networkHandler get() = player?.networkHandler
	inline var screen
		set(value) = instance.setScreen(value)
		get() = instance.currentScreen
	inline val screenName get() = screen?.title?.string
	inline val textureManager get() = instance.textureManager
	inline val resourceManager get() = instance.resourceManager
}