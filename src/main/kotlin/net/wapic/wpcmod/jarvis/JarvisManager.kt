package net.wapic.wpcmod.jarvis

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import moe.nea.jarvis.api.JarvisScalable
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.dungeons.ScoreCalculation
import net.wapic.wpcmod.features.dungeons.SpiritBearTimer
import net.wapic.wpcmod.features.dungeons.floor7.InactiveWaypoints
import net.wapic.wpcmod.features.dungeons.floor7.InvincibilityTimer
import net.wapic.wpcmod.features.dungeons.floor7.TickTimers
import net.wapic.wpcmod.features.dungeons.funnymap.ui.MapElement
import net.wapic.wpcmod.features.kuudra.KuudraDisplay
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object JarvisManager {

	private val file = File(WpcMod.configDir, "hud-locations.json")
	private val backupFile = File(file.parentFile, "${file.name}.bak")
	private val tempFile = File(file.parentFile, "${file.name}.tmp")
	private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

	private data class HudObject(val label: String, val x: Double, val y: Double, val scale: Float)

	val hudElements = listOf<JarvisScalable>(
		ScoreCalculation,
		MapElement,
		SpiritBearTimer,
		InactiveWaypoints,
		TickTimers,
		InvincibilityTimer,
		KuudraDisplay,
	)

	private fun readFile(): String = try {
		file.readText()
	} catch (e: Exception) {
		if (backupFile.exists()) backupFile.readText()
		else throw e
	}

	fun loadLocations() {
		if (!file.exists()) {
			WpcMod.logger.error("Could not find hud locations file. is this your first time launching?")
			return
		}

		try {
			WpcMod.logger.info("Loading hud locations file")
			val hudObjects = gson.fromJson(readFile(), Array<HudObject>::class.java).toList()

			hudObjects.forEach { hudObject ->
				val e = hudElements.find { it.label.string == hudObject.label }
				e?.scale = hudObject.scale
				e?.x = hudObject.x
				e?.y = hudObject.y
			}

			WpcMod.logger.info("Loaded hud locations successfully")
		} catch (e: Throwable) {
			WpcMod.logger.error("Failed to read hud locations file", e)
			val backup = file.resolveSibling("hud-locations-failed.json")
			try {
				WpcMod.logger.warn("Creating a backup of old file and loading default hud locations", e)
				file.copyTo(backup)
			} catch (e: Exception) {
				WpcMod.logger.error("Failed to backup hud locations file", e)
			}
		}
	}

	fun saveLocations() {
		try {
			if (!WpcMod.configDir.exists()) {
				WpcMod.configDir.mkdirs()
			}
			WpcMod.logger.info("Saving hud locations file")

			val huds = hudElements.map { return@map HudObject(it.label.string, it.x, it.y, it.scale) }
			tempFile.writeText(gson.toJson(huds))

			if (file.exists()) file.copyTo(backupFile, overwrite = true)

			Files.move(
				tempFile.toPath(),
				file.toPath(),
				StandardCopyOption.ATOMIC_MOVE,
				StandardCopyOption.REPLACE_EXISTING
			)

			backupFile.delete()
			WpcMod.logger.info("Hud locations saved Successfully")
		} catch (e: Exception) {
			tempFile.delete()
			if (backupFile.exists()) backupFile.copyTo(file, overwrite = true)
			WpcMod.logger.error("Failed to save hud locations file", e)
			throw e
		}
	}
}