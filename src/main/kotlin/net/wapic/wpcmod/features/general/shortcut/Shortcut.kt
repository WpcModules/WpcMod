package net.wapic.wpcmod.features.general.shortcut

import com.google.common.collect.Maps
import com.google.gson.annotations.Expose
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.network.chat.Component

class Shortcut {

	@Expose
	private var command: String

	@Expose
	private var keyCode: Int
	private var boundKey: InputConstants.Key

	private var timesPressed: Int = 0
	private var isPressed: Boolean = false

	constructor(command: String, key: InputConstants.Key) {
		this.command = command
		this.keyCode = key.value

		this.boundKey = key
		KEYS_BY_ID.add(this)
		KEY_TO_BINDINGS[key.value] = this
	}

	fun setBoundKey(key: InputConstants.Key) {
		this.keyCode = key.value
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
		return this.boundKey == InputConstants.UNKNOWN
	}

	fun getBoundKeyText(): Component {
		return this.boundKey.displayName
	}

	fun addToMap() {
		if (this.keyCode in 0..7) {
			this.boundKey = InputConstants.Type.MOUSE.getOrCreate(this.keyCode)
		} else {
			this.boundKey = InputConstants.Type.KEYSYM.getOrCreate(this.keyCode)
		}
		KEYS_BY_ID.add(this)
		KEY_TO_BINDINGS[this.keyCode] = this
	}

	companion object {

		private val KEYS_BY_ID: MutableList<Shortcut> = mutableListOf()
		private val KEY_TO_BINDINGS: MutableMap<Int, Shortcut> = Maps.newHashMap<Int, Shortcut>()

		fun updateKeysByCode() {
			KEY_TO_BINDINGS.clear()

			for (shortCut in KEYS_BY_ID) {
				KEY_TO_BINDINGS[shortCut.keyCode] = shortCut
			}
		}

		fun onKeyPressed(key: InputConstants.Key) {
			KEY_TO_BINDINGS[key.value]?.let {
				it.timesPressed++
			}
		}

		fun setKeyPressed(key: InputConstants.Key, pressed: Boolean) {
			KEY_TO_BINDINGS[key.value]?.let {
				it.isPressed = pressed
			}
		}
	}
}