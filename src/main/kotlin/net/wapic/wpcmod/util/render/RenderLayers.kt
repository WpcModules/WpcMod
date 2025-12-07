package net.wapic.wpcmod.util.render

import it.unimi.dsi.fastutil.doubles.Double2ObjectFunctions
import it.unimi.dsi.fastutil.doubles.Double2ObjectMap
import it.unimi.dsi.fastutil.doubles.Double2ObjectOpenHashMap
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderStateShard
import java.util.*
import java.util.function.DoubleFunction

object RenderLayers {

	val LINES_LAYERS: Double2ObjectMap<RenderType.CompositeRenderType> = Double2ObjectOpenHashMap()

	val LINES: DoubleFunction<RenderType.CompositeRenderType> = Double2ObjectFunctions.primitive { lineWidth ->
		RenderType.create(
			"wpcmod_lines",
			RenderType.TRANSIENT_BUFFER_SIZE,
			WpcModRenderPipelines.LINES,
			RenderType.CompositeState.builder()
				.setLineState(RenderStateShard.LineStateShard(OptionalDouble.of(lineWidth)))
				.setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING).createCompositeState(false)
		)
	}

	val FILLED_BOX: RenderType.CompositeRenderType = RenderType.create(
		"wpcmod_filled_box",
		RenderType.TRANSIENT_BUFFER_SIZE,
		WpcModRenderPipelines.FILLED_BOX,
		RenderType.CompositeState.builder()
				.setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING).createCompositeState(false)
		)

	fun getLines(lineWidth: Double): RenderType.CompositeRenderType {
		return LINES_LAYERS.computeIfAbsent(lineWidth, LINES)
	}
}