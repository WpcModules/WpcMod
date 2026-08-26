package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

object BlockEvents {

	@JvmField
	val CHANGE: Event<BlockChange> = EventFactory.createArrayBacked(BlockChange::class.java) { listeners ->
		BlockChange { pos, oldState, newState ->
			for (listener in listeners) {
				listener.onChange(pos, oldState, newState)
			}
		}
	}

	fun interface BlockChange {
		fun onChange(pos: BlockPos, oldState: BlockState?, newState: BlockState)
	}
}