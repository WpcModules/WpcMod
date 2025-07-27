package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import org.joml.Vector2i
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object TooltipEvents {

	@JvmField
	val POSITION: Event<TooltipPosition> = EventFactory.createArrayBacked(TooltipPosition::class.java) { listeners ->
		TooltipPosition { screenWidth, screenHeight, pos, width, height, callback ->
			for (listener in listeners) {
				listener.onPositionTooltip(screenWidth, screenHeight, pos, width, height, callback)
			}
		}
	}

	fun interface TooltipPosition {
		fun onPositionTooltip(
			screenWidth: Int,
			screenHeight: Int,
			position: Vector2i,
			width: Int,
			height: Int,
			callback: CallbackInfo
		)
	}

	@JvmField
	val RESET: Event<ResetTooltip> = EventFactory.createArrayBacked(ResetTooltip::class.java) { listeners ->
		ResetTooltip {
			for (listener in listeners) {
				listener.onTooltipReset()
			}
		}
	}

	fun interface ResetTooltip {
		fun onTooltipReset()
	}

}
