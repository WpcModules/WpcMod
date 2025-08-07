package net.wapic.wpcmod.config.general

import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.*
import net.minecraft.client.MinecraftClient
import net.wapic.wpcmod.features.general.shortcut.ShortcutScreen

class GeneralConfig {

	@ConfigOption(name = "Prevent Placing Items", desc = "Prevent placing items such as Weird Tuba and Flower of Truth")
	@ConfigEditorBoolean
	var preventPlacing: Boolean = false

	@ConfigOption(name = "Tag Outline Color", desc = "Color to use when highlighting players with /wpcmod tag command")
	@ConfigEditorColour
	var tagColor = ChromaColour(1f, 1f, 1f, 0, 0xFF)

	@Transient
	@ConfigOption(name = "Command Keybind Editor", desc = "Open the screen to manage command keybinds")
	@ConfigEditorButton(buttonText = "Open")
	val shortcutEditor = Runnable {
		MinecraftClient.getInstance().setScreen(ShortcutScreen())
	}
}