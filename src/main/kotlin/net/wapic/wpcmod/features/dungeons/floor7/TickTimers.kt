package net.wapic.wpcmod.features.dungeons.floor7

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.world.ClientWorld
import net.minecraft.network.listener.PacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket
import net.minecraft.text.Text
import net.minecraft.util.Colors
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.PacketEvents
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.jarvis.SimpleHudElement
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.toFixed

object TickTimers : SimpleHudElement("Tick Timers", h = 12, w = 120) {
	private val config get() = WpcMod.config.dungeon.floor7.tickTimers

    private val necronRegex = Regex("^\\[BOSS] Necron: I'm afraid, your journey ends now\\.$")
    private val goldorRegex = Regex("^\\[BOSS] Goldor: Who dares trespass into my domain\\?$")
    private val coreOpeningRegex = Regex("^The Core entrance is opening!$")
    private val stormStartRegex = Regex("^\\[BOSS] Storm: I should have known that I stood no chance\\.$")
    private val stormPadRegex = Regex("^\\[BOSS] Storm: Pathetic Maxor, just like expected\\.$")


    private var necronTime: Byte = -1
    private var goldorTickTime: Int = -1
    private var goldorStartTime: Int = -1

    private var padTickTime: Int = -1

	fun init() {
		PacketEvents.RECEIVE.register(::onPacketReceive)
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
		WorldChangeEvent.BEFORE.register(::onWorldChange)
	}

    fun onPacketReceive(packet: Packet<out PacketListener>) {
		if (!DungeonUtils.bossSpawned) return
        if (packet !is CommonPingS2CPacket) return

		if (goldorTickTime == 0 && goldorStartTime <= 0 && isActive) goldorTickTime = 60
		if (goldorStartTime >= 0 && isActive) goldorStartTime--
		if (goldorTickTime >= 0 && isActive) goldorTickTime--
		if (padTickTime == 0 && isActive) padTickTime = 20
		if (padTickTime >= 0 && isActive) padTickTime--
		if (necronTime >= 0 && isActive) necronTime--
    }

	fun onMessageReceived(text: Text, actionBar: Boolean) {
		if(actionBar) return

		when {
			isActive && text.string.matches(necronRegex) -> necronTime = 60
			isActive && text.string.matches(goldorRegex) -> goldorTickTime = 60
			isActive && text.string.matches(coreOpeningRegex) -> {
				goldorStartTime = -1
				goldorTickTime = -1
			}
			text.string.matches(stormStartRegex) -> {
				if (isActive) goldorStartTime = 104
				if (isActive) padTickTime = -1
			}
			isActive && text.string.matches(stormPadRegex) -> padTickTime = 20
		}
	}

    fun onWorldChange(world: ClientWorld) {
        goldorStartTime = -1
        goldorTickTime = -1
        padTickTime = -1
        necronTime = -1
    }

    private fun formatTimer(time: Int, max: Int, prefix: String): String {
        val color = when {
            time.toFloat() >= max * 0.66 -> "§a"
            time.toFloat() >= max * 0.33 -> "§6"
            else -> "§c"
        }
        val timeDisplay = if (config.displayInTicks) "$time${if (config.symbolDisplay) "t" else ""}" else "${(time / 20f).toFixed()}${if (config.symbolDisplay) "s" else ""}"
        return "${if (config.showPrefix) "$prefix " else ""}$color$timeDisplay"
    }

	override fun render(drawContext: DrawContext, renderTickCounter: RenderTickCounter) {
		if(!isActive) return
		val matrixStack = drawContext.matrices
		matrixStack.push()
		applyTransformations(matrixStack)
		val (prefix, time, max) =
			if(goldorStartTime >= 0 && config.startTimer)
				Triple("§aStart:", goldorStartTime, 104)
			else
				Triple("§7Tick:", goldorTickTime, 60)

		when {
			padTickTime >= 0 ->
				drawContext.drawText(MC.textRenderer, formatTimer(padTickTime, 20, "§bPad:"), 1, 1, Colors.RED, true)
			goldorStartTime >= 0 ->
				drawContext.drawText(MC.textRenderer, formatTimer(time, max, prefix), 1, 1, Colors.RED, true)
			necronTime >= 0 ->
				drawContext.drawText(MC.textRenderer, formatTimer(necronTime.toInt(), 60, "§4Necron dropping in"), 1, 1, Colors.RED, true)
		}
		matrixStack.pop()
	}

	override fun isActive(): Boolean {
		if(DungeonUtils.getF7Phase() == DungeonUtils.F7Phase.UNKNOWN) return false
		return super.isEnabled()
	}

	override fun isEnabled(): Boolean {
		return config.enabled
	}
}