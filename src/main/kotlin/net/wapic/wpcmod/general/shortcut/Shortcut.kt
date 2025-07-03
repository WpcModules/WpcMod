package net.wapic.wpcmod.general.shortcut

import com.google.common.collect.Maps
import com.google.gson.annotations.Expose
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW

class Shortcut {
    @Expose
    private var command: String
    @Expose
    private var keyCode: Int
    @Expose
    private var scanCode: Int

    constructor(command: String, keyCode: Int, scanCode: Int) {
        this.command = command
        this.keyCode = keyCode
        this.scanCode = scanCode
    }

    fun setBoundKey(keyCode: Int, scanCode: Int) {
        this.keyCode = keyCode
        this.scanCode = scanCode
    }

    fun getKeyCode(): Int {
        return this.keyCode
    }

    fun getScanCode(): Int {
        return this.keyCode
    }

    fun getCommand(): String {
        return this.command
    }

    fun setCommand(command: String) {
        this.command = command
    }

    fun equals(other: Shortcut): Boolean {
        return this.keyCode == other.keyCode
    }

    fun isUnbound(): Boolean {
        return this.keyCode == GLFW.GLFW_KEY_UNKNOWN
    }

    fun getBoundKeyText(): Text {
        return Text.of(GLFW.glfwGetKeyName(keyCode, scanCode))
    }

    companion object {
        private val KEYS_BY_ID: MutableMap<String, Shortcut> = Maps.newHashMap<String, Shortcut>()
        private val KEY_TO_BINDINGS: MutableMap<Int, Shortcut> = Maps.newHashMap<Int, Shortcut>()

        fun updateKeysByCode() {
            KEY_TO_BINDINGS.clear()

            for (shortCuts in KEYS_BY_ID.values) {
                KEY_TO_BINDINGS.put(shortCuts.keyCode, shortCuts)
            }
        }
    }

}