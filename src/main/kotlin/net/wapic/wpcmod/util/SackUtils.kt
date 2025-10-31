package net.wapic.wpcmod.util

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Util
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.ItemUtils.skyBlockID

object SackUtils {

	private val gfsQueue: MutableList<String> = mutableListOf()
	private var lastCommand: Long = 0
	private const val COMMAND_DELAY: Long = 2500

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
	}

	private fun onTick(client: MinecraftClient) {
		WpcMod.profiler.push("sack-utils-tick")
		if (gfsQueue.isEmpty()) return

		if (Util.getMeasuringTimeMs() - lastCommand >= COMMAND_DELAY) {
			val command = gfsQueue.first()
			Utils.runCommand(command)
			gfsQueue.removeFirst()
			lastCommand = Util.getMeasuringTimeMs()
		}
		WpcMod.profiler.pop()
	}

	fun getFromSack(item: String, maxStackSize: Int) {
		MinecraftClient.getInstance().player?.inventory?.let { inv ->
			val stackSize = inv.find { it.skyBlockID == item }?.count ?: 0
			if (stackSize != maxStackSize) gfsQueue.add("gfs $item ${maxStackSize - stackSize}")
		}
	}
}