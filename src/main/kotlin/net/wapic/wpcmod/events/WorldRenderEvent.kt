package net.wapic.wpcmod.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.profiling.ProfilerFiller
import net.wapic.wpcmod.util.render.WpcModExtractionContext

object WorldRenderEvent {

	@JvmField
	val EVENT: Event<WorldRender> = EventFactory.createArrayBacked(WorldRender::class.java) { listeners ->
		WorldRender { context, profiler ->
			for (listener in listeners) {
				listener.onRenderWorld(context, profiler)
			}
		}
	}

	fun interface WorldRender {

		fun onRenderWorld(extractionContext: WpcModExtractionContext, profiler: ProfilerFiller)
	}
}