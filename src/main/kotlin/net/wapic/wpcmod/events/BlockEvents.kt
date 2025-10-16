package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

object BlockEvents {

	@JvmField
	val CHANGE: Event<BlockChange> = EventFactory.createArrayBacked(BlockChange::class.java) { listeners ->
		BlockChange { pos, oldBlockState, newBlockState ->
			for (listener in listeners) {
				listener.onChange(pos, oldBlockState, newBlockState)
			}
		}
	}

	fun interface BlockChange {
		fun onChange(pos: BlockPos, oldBlockState: BlockState, newBlockState: BlockState)
	}
}