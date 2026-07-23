package net.wapic.wpcmod.util

import com.mojang.blaze3d.platform.Window
import net.minecraft.client.KeyboardHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.MouseHandler
import net.minecraft.client.Options
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.item.ItemStack
import net.wapic.wpcmod.features.general.Freecam
import net.wapic.wpcmod.mixin.accessors.MinecraftAccessor
import org.lwjgl.glfw.GLFW

object MC {

	fun runOnThread(run: () -> Unit) = instance.execute(run)
	fun useItem() = runOnThread { (instance as? MinecraftAccessor)?.doItemUse_WpcMod() }
	fun playSound(sound: SoundEvent, volume: Float, pitch: Float) =
		runOnThread { player?.playSound(sound, volume, pitch) }

	fun clickSlot(
		containerId: Int,
		slot: Int,
		button: Int = GLFW.GLFW_MOUSE_BUTTON_LEFT,
		input: ContainerInput = ContainerInput.PICKUP
	) {
		gameMode?.handleContainerInput(
			containerId,
			slot,
			button,
			input,
			player ?: return
		)
	}

	inline val entities get() = instance.level?.entitiesForRendering() ?: emptyList()
	inline val cameraPos get() = if (Freecam.isEnabled) instance.cameraEntity?.position() else instance.player?.position()
	inline fun <reified T> entitiesOf(): List<T> = entities.filterIsInstance<T>()

	inline val instance: Minecraft get() = Minecraft.getInstance()
	inline val player: LocalPlayer? get() = instance.player
	inline val font: Font get() = instance.font
	inline val level: ClientLevel? get() = instance.level
	inline val heldItem: ItemStack get() = player?.mainHandItem ?: ItemStack.EMPTY
	inline val window: Window get() = instance.window
	inline val gui: Gui get() = instance.gui
	inline val options: Options get() = instance.options
	inline val connection get() = player?.connection
	inline var screen: Screen?
		set(value) = gui.setScreen(value)
		get() = gui.screen()
	inline val screenName: String? get() = screen?.title?.string
	inline val textureManager: TextureManager get() = instance.textureManager
	inline val resourceManager: ResourceManager get() = instance.resourceManager
	inline val gameMode get() = instance.gameMode
	inline val mouse: MouseHandler get() = instance.mouseHandler
	inline val keyboard: KeyboardHandler get() = instance.keyboardHandler
}