package net.wapic.wpcmod.listeners

import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.common.ClientboundPingPacket
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.wapic.wpcmod.events.PacketEvents
import net.wapic.wpcmod.events.PlayerListChangeEvent
import net.wapic.wpcmod.events.ServerTickEvent
import net.wapic.wpcmod.features.dungeons.funnymap.utils.MapUtils

object NetworkListener {

	fun init() {
		PacketEvents.RECEIVE.register(::onPacketReceive)
	}

	private fun onPacketReceive(packet: Packet<*>) {
		when(packet) {
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

	private fun onPingPacket() {
		ServerTickEvent.EVENT.invoker().onServerTick()
	}
}