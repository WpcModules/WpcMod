package net.wapic.wpcmod.features.general.shortcut

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Utils
import java.io.File

class ShortcutHandler {

	init {
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)

		ClientLifecycleEvents.CLIENT_STARTED.register {
			loadShortcuts()
		}

		ClientLifecycleEvents.CLIENT_STOPPING.register {
			saveShortcuts()
		}
	}

	private fun onTick(client: MinecraftClient) {
		if (client.currentScreen != null) return

		loadedShortcuts.forEach { shortcut ->
			if (shortcut.isUnbound()) return@forEach

			while (shortcut.wasPressed()) {
				Utils.addToCommandQueue(shortcut.getCommand())
			}

		}
	}

	companion object {
		val saveFile = File(WpcMod.configDir, "shortcuts.json")
		val loadedShortcuts = mutableListOf<Shortcut>()
		private val gson: Gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create()

		fun loadShortcuts() {
			if (saveFile.exists()) {
				try {
					WpcMod.logger.info("Loading shortcuts")

					loadedShortcuts.addAll(gson.fromJson(saveFile.reader(), Array<Shortcut>::class.java).toList())
					loadedShortcuts.forEach(Shortcut::addToMap)
				} catch (e: Throwable) {
					WpcMod.logger.error("Failed to read shortcuts file", e)
					val backup = saveFile.resolveSibling("shortcuts-failed.json")
					try {
						WpcMod.logger.warn("Creating a backup of old file and loading default config", e)
						saveFile.copyTo(backup)
					} catch (e: Exception) {
						WpcMod.logger.error("Failed to backup config file", e)
					}
				}
			}
		}

		fun saveShortcuts() {
			try {
				WpcMod.logger.info("Saving shortcuts file")
				saveFile.parentFile.mkdirs()
				saveFile.createNewFile()
				saveFile.writeText(gson.toJson(loadedShortcuts))
			} catch (e: Exception) {
				WpcMod.logger.error("Failed to save shortcuts file", e)
				throw e
			}
		}
	}
}