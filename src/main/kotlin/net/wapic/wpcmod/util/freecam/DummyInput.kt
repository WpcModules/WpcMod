package net.wapic.wpcmod.util.freecam

import net.minecraft.client.player.KeyboardInput
import net.minecraft.client.Options

class DummyInput(options: Options?) : KeyboardInput(options) {
	override fun tick() {}
}