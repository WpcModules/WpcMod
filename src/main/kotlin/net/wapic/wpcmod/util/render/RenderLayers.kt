package net.wapic.wpcmod.util.render

import it.unimi.dsi.fastutil.doubles.Double2ObjectFunctions
import it.unimi.dsi.fastutil.doubles.Double2ObjectMap
import it.unimi.dsi.fastutil.doubles.Double2ObjectOpenHashMap
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderPhase
import java.util.OptionalDouble
import java.util.function.DoubleFunction

object RenderLayers {

    val LINES_LAYERS: Double2ObjectMap<RenderLayer.MultiPhase> = Double2ObjectOpenHashMap<RenderLayer.MultiPhase>()

    val LINES: DoubleFunction<RenderLayer.MultiPhase> = Double2ObjectFunctions.primitive { lineWidth ->
        RenderLayer.of(
            "wpcmod_lines",
            RenderLayer.DEFAULT_BUFFER_SIZE,
            WpcModRenderPipelines.LINES,
            RenderLayer.MultiPhaseParameters.builder()
                .lineWidth(RenderPhase.LineWidth(OptionalDouble.of(lineWidth)))
                .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                .build(false)
        )
    }


    val l = RenderLayer.of(
        "wpcmod:lines",
        RenderLayer.DEFAULT_BUFFER_SIZE,
        RenderPipelines.LINES,
        RenderLayer.MultiPhaseParameters.builder()
            .lineWidth(RenderPhase.LineWidth(OptionalDouble.of(1.0)))
            .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
            .build(false)
    )

    fun getLines(lineWidth: Double): RenderLayer.MultiPhase {
        return LINES_LAYERS.computeIfAbsent(lineWidth, LINES)
    }
}