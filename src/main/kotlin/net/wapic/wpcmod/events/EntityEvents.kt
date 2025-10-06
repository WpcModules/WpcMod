package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.entity.Entity

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
	val DESPAWN: Event<EntityDespawn> = EventFactory.createArrayBacked(EntityDespawn::class.java) { listeners ->
		EntityDespawn { entity ->
			for (listener in listeners) {
				listener.onEntityDespawn(entity)
			}
		}
	}

	fun interface EntityDespawn {
		fun onEntityDespawn(entity: Entity)
	}
}