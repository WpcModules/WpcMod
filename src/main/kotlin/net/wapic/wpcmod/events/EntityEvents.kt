package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack

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

	/**
	 * Called when an item is dropped/spawned,
	 * */
	@JvmField
	val ITEM_DATA_SET: Event<EntityItemSetData> =
		EventFactory.createArrayBacked(EntityItemSetData::class.java) { listeners ->
			EntityItemSetData { itemStack ->
				for (listener in listeners) {
					listener.onEntitySetItemData(itemStack)
				}
			}
		}

	fun interface EntityItemSetData {
		fun onEntitySetItemData(stack: ItemStack)
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