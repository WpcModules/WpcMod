package net.wapic.wpcmod.hud

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.KeyMapping
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.dungeons.ScoreCalculation
import net.wapic.wpcmod.features.dungeons.SpiritBearTimer
import net.wapic.wpcmod.features.dungeons.floor7.InactiveWaypoints
import net.wapic.wpcmod.features.dungeons.floor7.InvincibilityTimer
import net.wapic.wpcmod.features.dungeons.floor7.TickTimers
import net.wapic.wpcmod.features.dungeons.funnymap.ui.MapElement
import net.wapic.wpcmod.features.kuudra.KuudraDisplay
import net.wapic.wpcmod.features.slayer.GummyBearTimer
import net.wapic.wpcmod.util.MC
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object HudManager {

	private val file = File(WpcMod.configDir, "hud-locations.json")
	private val backupFile = File(file.parentFile, "${file.name}.bak")
	private val tempFile = File(file.parentFile, "${file.name}.tmp")
	private val gson: Gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create()

	private val hudKeyBind: KeyMapping = KeyBindingHelper.registerKeyBinding(
		KeyMapping(
			"hud",
			InputConstants.KEY_END,
			WpcMod.category
		)
	)

	private val hudElements = listOf(
		ScoreCalculation,
		KuudraDisplay,
		TickTimers,
		InactiveWaypoints,
		MapElement,
		SpiritBearTimer,
		InvincibilityTimer,
		GummyBearTimer,
	)

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register {
			while (hudKeyBind.consumeClick()) {
				openEditor()
			}
		}

		loadLocations()
	}

	fun openEditor() {
		MC.instance.schedule { MC.screen = HudEditor(hudElements) }
	}

	private fun readFile(): String = try {
		file.readText()
	} catch (e: Exception) {
		if (backupFile.exists()) backupFile.readText()
		else throw e
	}

	fun resetLocations() {
		hudElements.forEach {
			it.x = it.defaultX
			it.y = it.defaultY
			it.scale = it.defaultScale
		}
	}

	fun loadLocations() {
		if (!file.exists()) {
			WpcMod.LOGGER.warn("Could not find hud locations file. is this your first time launching?")
			return
		}

		try {
			WpcMod.LOGGER.info("Loading hud locations")
			val loadedElements = gson.fromJson(readFile(), Array<SimpleHudElement>::class.java).toList()

			loadedElements.forEach {
				val element = hudElements.find { element -> element.label == it.label }
				element?.x = it.x
				element?.y = it.y
				element?.scale = it.scale
			}

			WpcMod.LOGGER.info("Loaded hud locations successfully")
		} catch (e: Throwable) {
			WpcMod.LOGGER.error("Failed to read hud locations", e)
			try {
				val backup = file.resolveSibling("hud-locations-failed.json")
				WpcMod.LOGGER.warn("Creating a backup of old hud locations and loading defaults")
				file.copyTo(backup)
			} catch (e: Exception) {
				WpcMod.LOGGER.error("Failed to backup hud locations", e)
			}
		}
	}

	fun saveLocations() {
		try {
			if (!WpcMod.configDir.exists()) {
				WpcMod.configDir.mkdirs()
			}
			WpcMod.LOGGER.info("Saving hud locations")

			tempFile.writeText(gson.toJson(hudElements))

			if (file.exists()) file.copyTo(backupFile, overwrite = true)

			Files.move(
				tempFile.toPath(),
				file.toPath(),
				StandardCopyOption.ATOMIC_MOVE,
				StandardCopyOption.REPLACE_EXISTING
			)

			backupFile.delete()
			WpcMod.LOGGER.info("Hud locations saved successfully")
		} catch (e: Exception) {
			tempFile.delete()
			if (backupFile.exists()) backupFile.copyTo(file, overwrite = true)
			WpcMod.LOGGER.error("Failed to save hud locations", e)
			throw e
		}
	}
}