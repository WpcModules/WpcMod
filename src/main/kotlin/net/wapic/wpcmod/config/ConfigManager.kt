package net.wapic.wpcmod.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.LegacyStringChromaColourTypeAdapter
import io.github.notenoughupdates.moulconfig.gui.MoulConfigEditor
import io.github.notenoughupdates.moulconfig.processor.BuiltinMoulConfigGuis
import io.github.notenoughupdates.moulconfig.processor.ConfigProcessorDriver
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor
import net.wapic.wpcmod.WpcMod
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
        .registerTypeAdapter(ChromaColour::class.java, LegacyStringChromaColourTypeAdapter(true).nullSafe())
        .create()

    fun firstLoad() {
        setConfigHolder(firstLoadFile(WpcConfig::class.java.getDeclaredConstructor().newInstance()))
        recreateConfig()
    }

    private var jsonHolder: Any? = null

    private fun setConfigHolder(value: Any) {
        require(value.javaClass == WpcConfig::class.java)
        @Suppress("UNCHECKED_CAST")
        (WpcMod::config as KMutableProperty0<Any>).set(value)
        jsonHolder = value
    }

    fun firstLoadFile(defaultValue: Any): Any {
        WpcMod.logger.info("Attempting to load config file")
        var output: Any? = defaultValue

        if(file.exists()) {
            try {
                WpcMod.logger.info("Loading ${file.name}")
                val text = readText()
                output = gson.fromJson(text, defaultValue.javaClass)
            } catch (e: Throwable) {
                WpcMod.logger.error("Failed to read config file", e)
                val backup = file.resolveSibling("config-failed.json")
                try {
                    WpcMod.logger.warn("Creating a backup of old file and loading default config", e)
                    file.copyTo(backup)
                } catch (e: Exception) {
                    WpcMod.logger.error("Failed to backup config file", e)
                }
            }
        }

        if(output == null) {
            WpcMod.logger.info("Null file, falling back to default config")
            return defaultValue
        }

        WpcMod.logger.info("Config loaded successfully")
        return output
    }

    fun saveConfig() {
        try {
            WpcMod.logger.info("Saving config file")
            tempFile.writeText(gson.toJson(jsonHolder))
            if(file.exists()) file.copyTo(backupFile, overwrite = true)
            Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
            backupFile.delete()
        } catch (e: Exception) {
            tempFile.delete()
            if(backupFile.exists()) backupFile.copyTo(file, overwrite = true)
            WpcMod.logger.error("Failed to save config file", e)
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
        val driver = ConfigProcessorDriver(processor)
        driver.warnForPrivateFields = false
        driver.processConfig(WpcMod.config)
    }

    fun getEditor() = editor ?: MoulConfigEditor(processor).also { editor = it }
}