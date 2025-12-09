package net.wapic.wpcmod.config.general

import io.github.notenoughupdates.moulconfig.annotations.Category
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorButton
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import net.minecraft.client.Minecraft
import net.wapic.wpcmod.features.general.shortcut.ShortcutScreen
import net.wapic.wpcmod.hud.HudManager
import net.wapic.wpcmod.util.MC

class GeneralConfig {

	@ConfigOption(name = "Prevent Placing Items", desc = "Prevent placing items such as Weird Tuba and Flower of Truth")
	@ConfigEditorBoolean
	var preventPlacing: Boolean = false

	@Transient
	@ConfigOption(name = "Hud Editor", desc = "Open the Hud Editor")
	@ConfigEditorButton(buttonText = "Open")
	val hudEditor = Runnable { HudManager.openEditor() }

	@Transient
	@ConfigOption(name = "Command Keybind Editor", desc = "Open the screen to manage command keybinds")
	@ConfigEditorButton(buttonText = "Open")
	val shortcutEditor = Runnable { MC.screen = ShortcutScreen() }

	@Category(name = "ESP", desc = "Configure ESP Features")
	var esp: ESPConfig = ESPConfig()
}