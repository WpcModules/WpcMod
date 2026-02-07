package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.world.entity.Entity

object EntityEvents {

	@JvmField
	val SPAWN: Event<EntitySpawn> = EventFactory.createArrayBacked(EntitySpawn::class.java) { listeners ->
		EntitySpawn { entity ->
			for (listener in listeners) {
				listener.onSpawn(entity)
			}
		}
	}

	fun interface EntitySpawn {
		fun onSpawn(entity: Entity)
	}

	@JvmField
	val DEATH: Event<EntityDeath> = EventFactory.createArrayBacked(EntityDeath::class.java) { listeners ->
		EntityDeath { entity ->
			for (listener in listeners) {
				listener.onEntityDeath(entity)
			}
		}
	}

	fun interface EntityDeath {
		fun onEntityDeath(entity: Entity)
	}
}