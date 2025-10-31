package net.wapic.wpcmod.listeners

import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import net.wapic.wpcmod.events.EntityEvents
import net.wapic.wpcmod.events.PacketEvents
import net.wapic.wpcmod.events.PlayerListChangeEvent
import net.wapic.wpcmod.events.ServerTickEvent
import net.wapic.wpcmod.features.dungeons.funnymap.utils.MapUtils
import net.wapic.wpcmod.util.MC

object NetworkListener {

	fun init() {
		PacketEvents.RECEIVE.register(::onPacketReceive)
	}

	private fun onPacketReceive(packet: Packet<*>) {
		when(packet) {
			is PlayerListS2CPacket -> onTabListUpdate(packet)
			is EntitiesDestroyS2CPacket -> onEntityDespawn(packet)
			is EntitySpawnS2CPacket -> onEntitySpawn(packet)
			is MapUpdateS2CPacket -> MapUtils.updateMapData(packet)
			is CommonPingS2CPacket -> onPingPacket()
		}
	}

	private fun onTabListUpdate(packet: PlayerListS2CPacket) {
		val actions = setOf(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, PlayerListS2CPacket.Action.ADD_PLAYER)
		val hasActions = packet.actions.intersect(actions).isNotEmpty()
		if (hasActions) {
			PlayerListChangeEvent.EVENT.invoker().onPlayerListChange(packet.entries)
		}
	}

	private fun onEntityDespawn(packet: EntitiesDestroyS2CPacket){
		val world = MC.world ?: return

		packet.entityIds.forEach {
			val entity = world.getEntityById(it) ?: return@forEach
			EntityEvents.DESPAWN.invoker().onEntityDespawn(entity)
		}
	}

	private fun onEntitySpawn(packet: EntitySpawnS2CPacket) {
		val world = MC.world ?: return
		val entity = world.getEntity(packet.uuid) ?: return
		EntityEvents.SPAWN.invoker().onSpawn(entity)
	}

	private fun onPingPacket() {
		ServerTickEvent.EVENT.invoker().onServerTick()
	}
}