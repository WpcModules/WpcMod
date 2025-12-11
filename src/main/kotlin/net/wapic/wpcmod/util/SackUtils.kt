package net.wapic.wpcmod.util

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.Util
import net.wapic.wpcmod.util.ItemUtils.skyBlockID

object SackUtils {

	private val gfsQueue: MutableList<String> = mutableListOf()
	private var lastCommand: Long = 0
	private const val COMMAND_DELAY: Long = 2500

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
	}

	private fun onTick(client: Minecraft) {
		if (gfsQueue.isEmpty()) return
		if (Util.getMillis() - lastCommand >= COMMAND_DELAY) {
			val command = gfsQueue.first()
			Utils.runCommand(command)
			gfsQueue.removeFirst()
			lastCommand = Util.getMillis()
		}
	}

	fun getFromSack(item: String, maxStackSize: Int) {
		Minecraft.getInstance().player?.inventory?.let { inv ->
			val stackSize = inv.find { it.skyBlockID == item.uppercase() }?.count ?: 0
			if (stackSize < maxStackSize || stackSize != maxStackSize) gfsQueue.add("gfs $item ${maxStackSize - stackSize}")
		}
	}
}