package net.wapic.wpcmod.config

import com.google.gson.Gson
import io.github.notenoughupdates.moulconfig.gui.MoulConfigEditor
import io.github.notenoughupdates.moulconfig.processor.BuiltinMoulConfigGuis
import io.github.notenoughupdates.moulconfig.processor.ConfigProcessorDriver
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor
import net.wapic.wpcmod.WpcMod
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object ConfigManager {
    lateinit var config: WpcConfig
    lateinit var processor: MoulConfigProcessor<WpcConfig>
    val gson: Gson = Gson()

    var editor: MoulConfigEditor<WpcConfig>? = null
    val file = File("config/wpcmod/config.json")
    val tempFile = File("config/wpcmod/config-temp.json")
    val backupFile = File("config/wpcmod/config-backup.json")

    fun saveConfig(attempt: Int = 0){
        try {
            file.parentFile.mkdirs()
            tempFile.writeText(gson.toJson(config))
            if (file.exists()) file.copyTo(backupFile, overwrite = true)

            Files.move(
                tempFile.toPath(),
                file.toPath(),
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING,
            )

            backupFile.delete()
        } catch (e: AccessDeniedException) {
            if (attempt >= 5) throw e
            Thread.sleep(50L)
            saveConfig(attempt + 1)
        } catch (e: Exception) {
            tempFile.delete()
            if (backupFile.exists()) backupFile.copyTo(file, overwrite = true)
            throw e
        }
    }


    fun loadConfig(): WpcConfig? {
        if(file.exists()) {
            try {
                val text = loadFile()
                return gson.fromJson(text, WpcConfig::class.java)
            } catch (e: Exception) {
                WpcMod.logger.error(e.stackTraceToString())
            }
        }
        WpcMod.logger.info("Loading Default Config")
        return null
    }

    fun loadFile(): String = try {
       file.readText()
    } catch (e: Exception) {
        if(backupFile.exists()) backupFile.readText()
        else throw e
    }

    fun createConfig(){
        config = loadConfig() ?: WpcConfig()
        editor = null
        processor = MoulConfigProcessor(config)
        BuiltinMoulConfigGuis.addProcessors(processor)
        val driver = ConfigProcessorDriver(processor)
        driver.checkExpose = false
        driver.warnForPrivateFields = false
        driver.processConfig(config)
    }

    fun getEditorInstance(): MoulConfigEditor<*> = editor ?: MoulConfigEditor(processor)
}