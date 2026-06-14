package net.wapic.wpcmod.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.wapic.wpcmod.WpcMod
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object FileManager {
	val gson: Gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create()

	inline fun <reified T> loadFile(file: File, backupFile: File): T? {
		if (!file.exists()) {
			WpcMod.LOGGER.warn("Could not find ${file.name}.")
			return null
		}

		try {
			val fileToRead = try {
				WpcMod.LOGGER.info("Loading ${file.name}")
				file.readText()
			} catch (e: Exception) {
				WpcMod.LOGGER.warn("Failed to read ${file.name}, trying backup")
				if (backupFile.exists()) {
					backupFile.readText()
				} else {
					throw e
				}
			}

			val loadedFile = gson.fromJson(fileToRead, T::class.java)

			WpcMod.LOGGER.info("Loaded ${file.name} successfully")
			return loadedFile

		} catch (e: Throwable) {
			WpcMod.LOGGER.error("Failed to load ${file.name}", e)
			try {
				val failed = file.resolveSibling("${file.nameWithoutExtension}-failed.json")
				WpcMod.LOGGER.warn("Creating a backup of ${file.name} with name ${failed.name}")
				file.copyTo(failed)
			} catch (e: Exception) {
				WpcMod.LOGGER.error("Failed to backup ${file.name}", e)
				return null
			}
		}
		return null
	}

	fun saveFile(json: String, file: File, backupFile: File) {
		val tempFile = File(WpcMod.configDir, "${file.name}.tmp")

		try {
			if (!WpcMod.configDir.exists()) {
				WpcMod.configDir.mkdirs()
			}

			WpcMod.LOGGER.info("Saving ${file.name}")
			tempFile.writeText(json)

			if (file.exists()) file.copyTo(backupFile, overwrite = true)

			Files.move(
				tempFile.toPath(),
				file.toPath(),
				StandardCopyOption.ATOMIC_MOVE,
				StandardCopyOption.REPLACE_EXISTING
			)

			backupFile.delete()
			WpcMod.LOGGER.info("${file.name} saved successfully")
		} catch (e: Exception) {
			tempFile.delete()
			if (backupFile.exists()) {
				backupFile.copyTo(file, overwrite = true)
			}
			WpcMod.LOGGER.error("Failed to save ${file.name}, ", e)
			throw e
		}
	}
}