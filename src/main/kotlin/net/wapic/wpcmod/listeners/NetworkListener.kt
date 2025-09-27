package net.wapic.wpcmod.listeners

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import net.wapic.wpcmod.events.EntityEvents
import net.wapic.wpcmod.events.PacketEvents
import net.wapic.wpcmod.events.PlayerListChangeEvent

object NetworkListener {

	fun init() {
		PacketEvents.RECEIVE.register(::onPacketReceive)
	}

	private fun onPacketReceive(packet: Packet<*>) {
		val world = MinecraftClient.getInstance().world ?: return

		if (packet is PlayerListS2CPacket) {
			val actions =
				setOf(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, PlayerListS2CPacket.Action.UPDATE_LISTED)
			val hasActions = packet.actions.intersect(actions).isNotEmpty()
			if (hasActions) {
				PlayerListChangeEvent.EVENT.invoker().onPlayerListChange(packet.entries)
			}
		}

		if (packet is EntitiesDestroyS2CPacket) {
			packet.entityIds.forEach { entityId ->
				val entity = world.getEntityById(entityId)

				entity?.let {
					EntityEvents.DESPAWN.invoker().onEntityDespawn(it)
				}
			}
		}

		if (packet is EntitySpawnS2CPacket) {
			val entity = world.getEntityById(packet.entityId)

			entity?.let {
				EntityEvents.SPAWN.invoker().onSpawn(it)
			}
		}
	}
}