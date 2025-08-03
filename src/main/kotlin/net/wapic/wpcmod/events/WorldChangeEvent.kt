package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld

object WorldChangeEvent {

	@JvmField
	val EVENT: Event<WorldChange> = EventFactory.createArrayBacked(WorldChange::class.java) { listeners ->
		WorldChange { client, world ->
			for (listener in listeners) {
				listener.onWorldChange(client, world)
			}
		}
	}

	fun interface WorldChange {
		fun onWorldChange(client: MinecraftClient, world: ClientWorld)
	}
}
