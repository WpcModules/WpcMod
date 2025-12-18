package net.wapic.wpcmod.util

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.Util
import net.wapic.wpcmod.WpcMod
import java.util.*

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

	fun Any?.equalsOneOf(vararg other: Any): Boolean = other.any { this == it }
	fun modIdentifier(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(WpcMod.MOD_ID, path)
	fun Number.toFixed(decimals: Int = 2): String = "%.${decimals}f".format(Locale.ENGLISH, this)

	fun copyToClipboard(string: String) {
		MC.keyboard.clipboard = string
	}

	fun addToCommandQueue(command: String) {
		if (Util.getMillis() - lastCommand < MIN_DELAY || commandQueue.isNotEmpty()) {
			commandQueue.add(command)
			return
		}
		runCommand(command)
	}

	fun runCommand(command: String) {
		MC.networkHandler?.sendCommand(command.removePrefix("/"))
	}

	private fun onTick() {
		if (commandQueue.isEmpty()) return

		if (Util.getMillis() - lastCommand > MIN_DELAY) {
			runCommand(commandQueue.first())
			lastCommand = Util.getMillis()
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