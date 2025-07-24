package net.wapic.wpcmod.util.render

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.util.Identifier

object WpcModRenderPipelines {
	val LINES: RenderPipeline = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
			.withLocation(Identifier.of("wpcmod", "pipeline/wpcmod_lines"))
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.build()
	)
}