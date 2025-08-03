package net.wapic.wpcmod.util.freecam

import net.minecraft.client.input.KeyboardInput
import net.minecraft.client.option.GameOptions

class DummyInput(options: GameOptions?) : KeyboardInput(options) {
	override fun tick() {}
}