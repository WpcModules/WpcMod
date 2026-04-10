package net.wapic.wpcmod.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.LegacyStringChromaColourTypeAdapter
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.gui.MoulConfigEditor
import io.github.notenoughupdates.moulconfig.processor.BuiltinMoulConfigGuis
import io.github.notenoughupdates.moulconfig.processor.ConfigProcessorDriver
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.components.slider.GuiOptionSlider
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.reflect.KMutableProperty0

object ConfigManager {

	private val file = File(WpcMod.configDir, "config.json")
	private val backupFile = File(file.parentFile, "${file.name}.bak")
	private val tempFile = File(file.parentFile, "${file.name}.tmp")
	private var editor: MoulConfigEditor<WpcConfig>? = null
	private lateinit var processor: MoulConfigProcessor<WpcConfig>
	private val gson: Gson = GsonBuilder().setPrettyPrinting()
		.registerTypeAdapter(ChromaColour::class.java, LegacyStringChromaColourTypeAdapter(true).nullSafe()).create()

	fun firstLoad() {
		setConfigHolder(firstLoadFile(WpcConfig::class.java.getDeclaredConstructor().newInstance()))
		recreateConfig()
	}

	private var jsonHolder: Any? = null

	private fun setConfigHolder(value: Any) {
		require(value.javaClass == WpcConfig::class.java)
		@Suppress("UNCHECKED_CAST") (WpcMod::config as KMutableProperty0<Any>).set(value)
		jsonHolder = value
	}

	fun firstLoadFile(defaultValue: Any): Any {
		var output: Any? = defaultValue

		if (file.exists()) {
			try {
				WpcMod.LOGGER.info("Loading ${file.name}")
				val text = readText()
				output = gson.fromJson(text, defaultValue.javaClass)
			} catch (e: Throwable) {
				WpcMod.LOGGER.error("Failed to read config file", e)
				val backup = file.resolveSibling("config-failed.json")
				try {
					WpcMod.LOGGER.warn("Creating a backup of old config and loading default config", e)
					file.copyTo(backup)
				} catch (e: Exception) {
					WpcMod.LOGGER.error("Failed to backup config", e)
				}
			}
		}

		if (output == null) {
			WpcMod.LOGGER.info("Null file, falling back to default config")
			return defaultValue
		}

		WpcMod.LOGGER.info("Config loaded successfully")
		return output
	}

	fun saveConfig() {
		try {
			if (!WpcMod.configDir.exists()) {
				WpcMod.configDir.mkdirs()
			}
			WpcMod.LOGGER.info("Saving config")
			tempFile.writeText(gson.toJson(jsonHolder))
			if (file.exists()) file.copyTo(backupFile, overwrite = true)
			Files.move(
				tempFile.toPath(), file.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING
			)
			backupFile.delete()
			WpcMod.LOGGER.info("Config saved successfully")
		} catch (e: Exception) {
			tempFile.delete()
			if (backupFile.exists()) backupFile.copyTo(file, overwrite = true)
			WpcMod.LOGGER.error("Failed to save config", e)
			throw e
		}
	}

	private fun readText(): String = try {
		file.readText()
	} catch (e: Exception) {
		if (backupFile.exists()) backupFile.readText()
		else throw e
	}

	private fun recreateConfig() {
		editor = null
		processor = MoulConfigProcessor(WpcMod.config)
		BuiltinMoulConfigGuis.addProcessors(processor)
		processor.registerConfigEditor(ConfigEditorSlider::class.java) { option, configEditorSlider ->
			GuiOptionSlider(
				option,
				configEditorSlider.minValue,
				configEditorSlider.maxValue,
				configEditorSlider.minStep
			)
		}
		val driver = ConfigProcessorDriver(processor)
		driver.warnForPrivateFields = false
		driver.checkExpose = false
		driver.processConfig(WpcMod.config)
	}

	fun getEditor() = editor ?: MoulConfigEditor(processor).also { editor = it }
}