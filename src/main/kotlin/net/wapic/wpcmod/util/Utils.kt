package net.wapic.wpcmod.util

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Util
import net.wapic.wpcmod.WpcMod

object Utils {
	private const val MIN_DELAY: Long = 500
	private val commandQueue = mutableListOf<String>()
	private var lastCommand: Long = 0

	private var location: Island? = null

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register { onTick() }
		HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket::class.java)
		HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket::class.java, ::onHypixelLocationPacket)
	}

	fun addToCommandQueue(command: String) {
		if (Util.getMeasuringTimeMs() - lastCommand < MIN_DELAY || commandQueue.isNotEmpty()) {
			commandQueue.add(command)
			return
		}
		runCommand(command)
	}

	fun runCommand(command: String) {
		MinecraftClient.getInstance().networkHandler?.sendCommand(command.removePrefix("/"))
	}

	private fun onTick() {
		if (commandQueue.isEmpty()) return

		if (Util.getMeasuringTimeMs() - lastCommand > MIN_DELAY) {
			runCommand(commandQueue.first())
			lastCommand = Util.getMeasuringTimeMs()
			commandQueue.removeFirst()
		}
	}

	fun getLocation(): Island? {
		return location
	}

	private fun onHypixelLocationPacket(packet: ClientboundLocationPacket) {
		if (packet.map.isPresent) {
			location = Island.fromDisplayName(packet.map.get())
			WpcMod.logger.info("Map set to: $location")
		}
	}
}