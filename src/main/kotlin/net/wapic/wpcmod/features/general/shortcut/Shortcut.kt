package net.wapic.wpcmod.features.general.shortcut

import com.google.common.collect.Maps
import com.google.gson.annotations.Expose
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text

class Shortcut {
    @Expose
    private var command: String
    @Expose
    private var keyCode: Int
    private var boundKey: InputUtil.Key

    private var timesPressed: Int = 0
    private var isPressed: Boolean = false

    constructor(command: String, key: InputUtil.Key) {
        this.command = command
        this.keyCode = key.code

        this.boundKey = key
        KEYS_BY_ID.add(this)
        KEY_TO_BINDINGS.put(key.code, this)
    }

    fun setBoundKey(key: InputUtil.Key) {
        this.keyCode = key.code
        this.boundKey = key
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
        return this.boundKey == InputUtil.UNKNOWN_KEY
	}

    fun getBoundKeyText(): Text {
        return this.boundKey.localizedText
    }

    fun addToMap(){
        if(this.keyCode in 0..7) {
            this.boundKey = InputUtil.Type.MOUSE.createFromCode(this.keyCode)
        } else {
            this.boundKey = InputUtil.Type.KEYSYM.createFromCode(this.keyCode)
        }
        KEYS_BY_ID.add(this)
        KEY_TO_BINDINGS.put(this.keyCode, this)
    }

    companion object {
        private val KEYS_BY_ID: MutableList<Shortcut> = mutableListOf()
        private val KEY_TO_BINDINGS: MutableMap<Int, Shortcut> = Maps.newHashMap<Int, Shortcut>()

        fun updateKeysByCode() {
            KEY_TO_BINDINGS.clear()

            for (shortCut in KEYS_BY_ID) {
                KEY_TO_BINDINGS.put(shortCut.keyCode, shortCut)
            }
        }

        fun onKeyPressed(key: InputUtil.Key) {
            KEY_TO_BINDINGS[key.code]?.let {
                it.timesPressed++
            }
        }

        fun setKeyPressed(key: InputUtil.Key, pressed: Boolean) {
            KEY_TO_BINDINGS[key.code]?.let {
                it.isPressed = pressed
            }
        }
    }
}