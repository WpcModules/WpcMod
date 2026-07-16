package net.wapic.wpcmod.features.general.shortcut

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.FileManager
import net.wapic.wpcmod.util.Utils
import java.io.File

object ShortcutHandler {
	private val file = File(WpcMod.configDir, "shortcuts.json")
	private val backupFile = File(file.parentFile, "${file.name}.bak")
	private val gson: Gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create()
	val loadedShortcuts = mutableListOf<Shortcut>()

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)

		ClientLifecycleEvents.CLIENT_STARTED.register {
			loadShortcuts()
		}

		ClientLifecycleEvents.CLIENT_STOPPING.register {
			saveShortcuts()
		}
	}

	private fun onTick(client: Minecraft) {
		if (client.gui.screen() != null) return

		loadedShortcuts.forEach { shortcut ->
			if (shortcut.isUnbound()) return@forEach

			while (shortcut.wasPressed()) {
				Utils.addToCommandQueue(shortcut.getCommand())
			}

		}
	}

	fun loadShortcuts() {
		val shortcuts = FileManager.loadFile<Array<Shortcut>>(file, backupFile)?.toList()
		shortcuts?.let {
			loadedShortcuts.addAll(shortcuts)
			loadedShortcuts.forEach(Shortcut::addToMap)
		} ?: return WpcMod.LOGGER.info("An error while loading shortcuts!")
	}

	fun saveShortcuts() {
		val json = gson.toJson(loadedShortcuts)
		FileManager.saveFile(json, file, backupFile)
	}
}