package net.wapic.wpcmod.features.hunting

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.ParticleEvents
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.util.SafariAPI
import net.wapic.wpcmod.util.Utils

object SafariSparklingHelper {
	private val config get() = WpcMod.config.hunting.safari
	val foundSparkling = mutableSetOf<String>()

	fun init() {
		ParticleEvents.SPAWN.register(::onParticleSpawn)
		WorldChangeEvent.AFTER.register {
			foundSparkling.clear()
		}
	}

	fun onParticleSpawn(packet: ClientboundLevelParticlesPacket, level: ClientLevel) {
		if (!SafariAPI.inSafari || !config.announceSparkling) return
		if (packet.particle.type != ParticleTypes.WAX_ON) return

		val entityNameTag = level.getEntitiesOfClass(
			ArmorStand::class.java,
			AABB.ofSize(Vec3(packet.x, packet.y, packet.z), 1.0, 1.0, 1.0),
			EntitySelector.ENTITY_NOT_BEING_RIDDEN
		).firstOrNull() ?: return

		if (foundSparkling.contains(entityNameTag.stringUUID)) return

		val critter =
			SafariAPI.Critter.entries.firstOrNull { entityNameTag.name.string.contains(it.entityName) } ?: return
		Utils.runCommand("pc ${entityNameTag.blockPosition().toShortString()} Sparkling ${critter.entityName} found")
		foundSparkling.add(entityNameTag.stringUUID)
	}
}