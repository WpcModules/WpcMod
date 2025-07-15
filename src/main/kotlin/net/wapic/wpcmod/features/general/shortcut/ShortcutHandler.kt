package net.wapic.wpcmod.features.general.shortcut

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Utils
import org.lwjgl.glfw.GLFW
import java.io.File

class ShortcutHandler {

    var canTrigger = false

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
        if(client.currentScreen != null) return

        allShortcuts.forEach { shortcut ->
           if(shortcut.getKeyCode() == GLFW.GLFW_KEY_UNKNOWN || shortcut.getScanCode() == GLFW.GLFW_KEY_UNKNOWN) return@forEach

           if(GLFW.glfwGetKey(client.window.handle, shortcut.getKeyCode()) == GLFW.GLFW_PRESS && canTrigger) {
               Utils.addToCommandQueue(shortcut.getCommand())
               canTrigger = false
           }

           if(GLFW.glfwGetKey(client.window.handle, shortcut.getKeyCode()) == GLFW.GLFW_RELEASE) {
               canTrigger = true
           }
        }
    }

    companion object {
        val allShortcuts: MutableList<Shortcut> = mutableListOf()
        val saveFile = File("./config/wpcmod").resolve("shortcuts.json")
        private val gson: Gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create()

        fun loadShortcuts() {
            if(saveFile.exists()) {
                try {
                    WpcMod.logger.info("Loading shortcuts")
                    val loadedShortcuts: List<Shortcut> = gson.fromJson(saveFile.reader(), Array<Shortcut>::class.java).toList()
                    allShortcuts.addAll(loadedShortcuts)
                } catch (e: Throwable) {
                    WpcMod.logger.error("Failed to read shotcuts file", e)
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
                saveFile.mkdirs()
                saveFile.writeText(gson.toJson(allShortcuts))
            } catch (e: Exception) {
                WpcMod.logger.error("Failed to save shortcuts file", e)
                throw e
            }
        }
    }
}