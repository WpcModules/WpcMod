package net.wapic.wpcmod.listeners

import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.common.ClientboundPingPacket
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.wapic.wpcmod.events.PacketEvents
import net.wapic.wpcmod.events.PlayerListChangeEvent
import net.wapic.wpcmod.events.ServerTickEvent
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.features.dungeons.funnymap.utils.MapUtils
import net.wapic.wpcmod.util.RunOnStartup

object NetworkListener {

	private var tick: Long = 0L

	@RunOnStartup
	fun init() {
		PacketEvents.RECEIVE.register(::onPacketReceive)
		WorldChangeEvent.AFTER.register { tick = 0L }
	}

	private fun onPacketReceive(packet: Packet<*>) {
		when (packet) {
			is ClientboundPlayerInfoUpdatePacket -> onTabListUpdate(packet)
			is ClientboundMapItemDataPacket -> MapUtils.updateMapData(packet)
			is ClientboundPingPacket -> onPingPacket()
		}
	}

	private fun onTabListUpdate(packet: ClientboundPlayerInfoUpdatePacket) {
		val actions = setOf(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER)
		val hasActions = packet.actions().intersect(actions).isNotEmpty()
		if (hasActions) {
			PlayerListChangeEvent.EVENT.invoker().onPlayerListChange(packet.entries())
		}
	}

	private fun onPingPacket() = ServerTickEvent.EVENT.invoker().onServerTick(tick++)
}