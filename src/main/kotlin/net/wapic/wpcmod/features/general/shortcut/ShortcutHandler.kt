package net.wapic.wpcmod.features.general.shortcut

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.wapic.wpcmod.util.Utils
import org.lwjgl.glfw.GLFW
import java.io.File

class ShortcutHandler {

    var canTrigger = false

    init {
        ClientTickEvents.END_CLIENT_TICK.register(::onTick)

        ClientLifecycleEvents.CLIENT_STARTED.register {
            if(saveFile.exists() && !saveFile.readLines().isEmpty()){
                val loadedShortcuts: List<Shortcut> = gson.fromJson(saveFile.reader(), Array<Shortcut>::class.java).toList()
                allShortcuts.addAll(loadedShortcuts)
            }
        }

        ClientLifecycleEvents.CLIENT_STOPPING.register {
            saveShortcuts()
        }
    }

    private fun onTick(client: MinecraftClient){
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

        fun saveShortcuts() {
            saveFile.writeText(gson.toJson(allShortcuts))
        }
    }
}