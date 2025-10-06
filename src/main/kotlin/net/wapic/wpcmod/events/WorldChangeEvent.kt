package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.world.ClientWorld

object WorldChangeEvent {

	@JvmField
	val BEFORE: Event<WorldChangeBefore> = EventFactory.createArrayBacked(WorldChangeBefore::class.java) { listeners ->
		WorldChangeBefore { world ->
			for (listener in listeners) {
				listener.onWorldChange(world)
			}
		}
	}

	fun interface WorldChangeBefore {
		fun onWorldChange(world: ClientWorld)
	}

	@JvmField
	val AFTER: Event<WorldChangeAfter> = EventFactory.createArrayBacked(WorldChangeAfter::class.java) { listeners ->
		WorldChangeAfter { world ->
			for (listener in listeners) {
				listener.onWorldChange(world)
			}
		}
	}

	fun interface WorldChangeAfter {
		fun onWorldChange(world: ClientWorld)
	}
}
