package net.wapic.wpcmod.features.general.shortcut

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Utils
import java.io.File

object ShortcutHandler {
	private val saveFile = File(WpcMod.configDir, "shortcuts.json")
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
		if (client.screen != null) return

		loadedShortcuts.forEach { shortcut ->
			if (shortcut.isUnbound()) return@forEach

			while (shortcut.wasPressed()) {
				Utils.addToCommandQueue(shortcut.getCommand())
			}

		}
	}

	fun loadShortcuts() {
		if (saveFile.exists()) {
			try {
				WpcMod.LOGGER.info("Loading shortcuts")

				loadedShortcuts.addAll(gson.fromJson(saveFile.reader(), Array<Shortcut>::class.java).toList())
				loadedShortcuts.forEach(Shortcut::addToMap)
			} catch (e: Throwable) {
				WpcMod.LOGGER.error("Failed to read shortcuts", e)
				try {
					val backup = saveFile.resolveSibling("shortcuts-failed.json")
					WpcMod.LOGGER.warn("Creating a backup of old shortcuts and continuing with empty shortcuts", e)
					saveFile.copyTo(backup)
				} catch (e: Exception) {
					WpcMod.LOGGER.error("Failed to backup old shortcuts", e)
				}
			}
		}
	}

	fun saveShortcuts() {
		try {
			WpcMod.LOGGER.info("Saving shortcuts file")
			saveFile.parentFile.mkdirs()
			saveFile.createNewFile()
			saveFile.writeText(gson.toJson(loadedShortcuts))
		} catch (e: Exception) {
			WpcMod.LOGGER.error("Failed to save shortcuts file", e)
			throw e
		}
	}
}