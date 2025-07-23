package net.wapic.wpcmod.features.general.shortcut

import com.google.common.collect.Maps
import com.google.gson.annotations.Expose
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW

class Shortcut {
    @Expose
    private var command: String
    @Expose
    private var keyCode: Int
    @Expose
    private var scanCode: Int

    private var timesPressed: Int = 0
    private var isPressed: Boolean = false

    constructor(command: String, keyCode: Int, scanCode: Int) {
        this.command = command
        this.keyCode = keyCode
        this.scanCode = scanCode
        KEYS_BY_ID.add(this)
        KEY_TO_BINDINGS.put(this.keyCode, this)
    }

    fun setBoundKey(keyCode: Int, scanCode: Int) {
        this.keyCode = keyCode
        this.scanCode = scanCode
    }

    fun wasPressed(): Boolean {
        if (this.timesPressed == 0) {
            return false
        } else {
            --this.timesPressed
            return true
        }
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
        return InputUtil.fromKeyCode(this.keyCode, this.scanCode).localizedText
    }

    fun addToMap(){
        KEYS_BY_ID.add(this)
        KEY_TO_BINDINGS.put(this.keyCode, this)
    }

    companion object {
        private val KEYS_BY_ID: MutableList<Shortcut> = mutableListOf()
        private val KEY_TO_BINDINGS: MutableMap<Int, Shortcut> = Maps.newHashMap<Int, Shortcut>()

        fun updateKeysByCode() {
            KEY_TO_BINDINGS.clear()

            for (shortCuts in KEYS_BY_ID) {
                KEY_TO_BINDINGS.put(shortCuts.keyCode, shortCuts)
            }
        }

        fun onKeyPressed(key: Int) {
            KEY_TO_BINDINGS[key]?.let {
                it.timesPressed++
            }
        }

        fun setKeyPressed(key: Int, pressed: Boolean) {
            KEY_TO_BINDINGS[key]?.let {
                it.isPressed = pressed
            }
        }
    }
}