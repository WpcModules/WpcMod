package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.wapic.wpcmod.util.render.WorldRenderContext

object WorldRenderEvent {

	@JvmField
	val EVENT: Event<WorldRender> = EventFactory.createArrayBacked(WorldRender::class.java) { listeners ->
		WorldRender { renderContext ->
			for (listener in listeners) {
				listener.onRenderWorld(renderContext)
			}
		}
	}

	fun interface WorldRender {
		fun onRenderWorld(context: WorldRenderContext)
	}
}