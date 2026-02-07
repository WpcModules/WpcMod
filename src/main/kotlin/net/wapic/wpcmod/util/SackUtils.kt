package net.wapic.wpcmod.util

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.wapic.wpcmod.util.ItemUtils.skyBlockID

object SackUtils {

	private val gfsQueue: ArrayDeque<String> = ArrayDeque()
	private var lastCommand: Long = 0
	private const val COMMAND_DELAY: Long = 2000 // Hypixel Command Rate Limitation
	private val gfsRegex = Regex("^Moved (?<amount>\\d+) (?<item>.+) from your Sacks to your inventory\\.$")
	private var gfsLock = false

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
	}

	private fun onTick(client: Minecraft) {
		if (gfsQueue.isEmpty()) return
		if (Util.getMillis() - lastCommand >= COMMAND_DELAY) {
			val command = gfsQueue.removeFirstOrNull() ?: return
			Utils.runCommand(command)
			lastCommand = Util.getMillis()
		}
	}

	private fun onMessageReceived(text: Component, actionBar: Boolean) {
		if(actionBar) return

		if (text.string.matches(gfsRegex)) gfsLock = false
	}

	fun queueGetFromSack(item: String, maxStackSize: Int) {
		val inv = MC.player?.inventory ?: return
		val stackSize = inv.find { it.skyBlockID == item.uppercase() }?.count ?: 0

		if (stackSize < maxStackSize || stackSize != maxStackSize) {
			gfsQueue.add("gfs $item ${maxStackSize - stackSize}")
		}
	}

	fun getFromSack(item: String, maxStackSize: Int) {
		if(gfsLock || Util.getMillis() - lastCommand <= COMMAND_DELAY) return

		val inv = MC.player?.inventory ?: return
		val stackSize = inv.find { it.skyBlockID == item.uppercase() }?.count ?: 0

		if (stackSize < maxStackSize || stackSize != maxStackSize) {
			Utils.runCommand("gfs $item ${maxStackSize - stackSize}")
			lastCommand = Util.getMillis()
			gfsLock = true
		}
	}
}