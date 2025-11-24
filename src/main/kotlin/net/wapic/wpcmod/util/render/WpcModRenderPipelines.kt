package net.wapic.wpcmod.util.render

import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.blaze3d.vertex.VertexFormatElement
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.render.VertexFormats
import net.wapic.wpcmod.util.Utils.modIdentifier

object WpcModRenderPipelines {

	val LINES: RenderPipeline = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
			.withLocation(modIdentifier("pipeline/wpcmod_lines"))
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).build()
	)

	val FILLED_BOX: RenderPipeline = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
			.withLocation(modIdentifier("pipeline/wpcmod_filled_box"))
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP).build()
	)

	val GUI_THING: RenderPipeline = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
			.withBlend(BlendFunction.TRANSLUCENT)
			.withVertexFormat(
				VertexFormat.builder()
					.add("Position", VertexFormatElement.POSITION)
					.add("UV0", VertexFormatElement.UV0)
					.add(
						"UV1",
						VertexFormatElement.register(
							getNextVFId(),
							0,
							VertexFormatElement.Type.FLOAT,
							VertexFormatElement.Usage.UV,
							2
						)
					)
					.add(
						"Roundness",
						VertexFormatElement.register(
							getNextVFId(),
							0,
							VertexFormatElement.Type.FLOAT,
							VertexFormatElement.Usage.UV,
							4
						)
					)
					.add("Color", VertexFormatElement.COLOR)
					.build(), VertexFormat.DrawMode.QUADS
			)
			.withCull(true)
			.withFragmentShader(modIdentifier("core/rendertype_rr"))
			.withVertexShader(modIdentifier("core/rendertype_rr"))
			.withLocation(modIdentifier("pipeline/2d/quad_rr"))
			.build()
	)

	private fun getNextVFId(): Int {
		for (i in 0..<VertexFormatElement.MAX_COUNT) {
			if (VertexFormatElement.byId(i) == null) return i
		}
		throw IllegalStateException("No more free VertexFormatElement slots")
	}
}