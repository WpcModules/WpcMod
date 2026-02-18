package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object PlayerPickEvents {

	@JvmField
	val BLOCK: Event<PickBlock> = EventFactory.createArrayBacked(PickBlock::class.java) { listeners ->
		PickBlock { blockPos, callbackInfo ->
			for (listener in listeners) {
				listener.onPickBlock(blockPos, callbackInfo)
			}
		}
	}

	fun interface PickBlock {
		fun onPickBlock(blockPos: BlockPos, callbackInfo: CallbackInfo)
	}

	@JvmField
	val ENTITY: Event<PickEntity> = EventFactory.createArrayBacked(PickEntity::class.java) { listeners ->
		PickEntity { entity, pos, callbackInfo ->
			for (listener in listeners) {
				listener.onPickEntity(entity, pos, callbackInfo)
			}
		}
	}

	fun interface PickEntity {
		fun onPickEntity(entity: Entity, pos: Vec3, callbackInfo: CallbackInfo)
	}
}