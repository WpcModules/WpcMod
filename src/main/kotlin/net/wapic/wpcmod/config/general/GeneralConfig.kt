package net.wapic.wpcmod.config.general

import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.*
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
	val shortcutEditor = Runnable {
		MC.screen = ShortcutScreen(MC.screen)
	}

	@Accordion
	@ConfigOption(name = "Century Cake Helper", desc = "")
	val centuryCake: CenturyCakeConfig = CenturyCakeConfig()

	class CenturyCakeConfig {
		@ConfigOption(name = "Enable Century Cake Helper", desc = "Enables the Century Cake Helper")
		@ConfigEditorBoolean
		var enabled: Boolean = false

		@ConfigOption(name = "Highlight Eaten Cakes", desc = "Highlights cakes that have been eaten")
		@ConfigEditorBoolean
		var highlightEaten: Boolean = false

		@ConfigOption(name = "Cake Eaten Color", desc = "Highlight color when a cake has been eaten")
		@ConfigEditorColour
		var cakeEatenColor: ChromaColour = ChromaColour.fromStaticRGB(255, 0, 0, 255)

		@ConfigOption(name = "Cake Ready Color", desc = "Highlight color when a cake is ready to be eaten")
		@ConfigEditorColour
		var cakeReadyColor: ChromaColour = ChromaColour.fromStaticRGB(0, 255, 0, 255)
	}

	@Category(name = "ESP", desc = "Configure general ESP features")
	var esp: ESPConfig = ESPConfig()
}