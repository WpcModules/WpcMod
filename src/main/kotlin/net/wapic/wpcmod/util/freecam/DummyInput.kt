package net.wapic.wpcmod.util.freecam

import net.minecraft.client.Options
import net.minecraft.client.player.KeyboardInput

class DummyInput(options: Options) : KeyboardInput(options) {
	override fun tick() {}
}