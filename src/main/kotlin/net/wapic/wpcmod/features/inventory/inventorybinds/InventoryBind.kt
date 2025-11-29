package net.wapic.wpcmod.features.inventory.inventorybinds

import com.google.gson.annotations.Expose
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text

class InventoryBind(
	@Expose var slot: Int,
	var key: InputUtil.Key
) {

	@Expose private var keyCode: Int = key.code

	fun setBoundKey(key: InputUtil.Key) {
		this.keyCode = key.code
		this.key = key
	}

	fun isUnbound(): Boolean {
		return this.key == InputUtil.UNKNOWN_KEY
	}

	fun getBoundKeyText(): Text {
		return this.key.localizedText
	}
}