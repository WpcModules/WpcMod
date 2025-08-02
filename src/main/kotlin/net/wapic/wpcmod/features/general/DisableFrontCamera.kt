package net.wapic.wpcmod.features.general

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.Perspective
import net.wapic.wpcmod.WpcMod

class DisableFrontCamera {

	private val config get() = WpcMod.config.general

	init {
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
	}

	fun onTick(client: MinecraftClient) {
		if (!config.disableFrontCamera) return

		if (client.options.perspective.isFrontView) {
			client.options.perspective = Perspective.FIRST_PERSON
		}
	}
}