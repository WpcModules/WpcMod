package net.wapic.wpcmod.util.render

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import com.mojang.blaze3d.vertex.VertexFormat
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
}