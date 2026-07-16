package net.wapic.wpcmod.util.render

import com.mojang.blaze3d.pipeline.RenderPipeline
import net.minecraft.client.renderer.RenderPipelines
import net.wapic.wpcmod.util.Utils.modIdentifier
import java.util.*

object WpcModRenderPipelines {

	val LINES: RenderPipeline = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withLocation(modIdentifier("pipeline/wpcmod_lines"))
			.withDepthStencilState(Optional.empty())
			.build()
	)

	val FILLED_BOX: RenderPipeline = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withLocation(modIdentifier("pipeline/wpcmod_filled_box"))
			.withDepthStencilState(Optional.empty())
			.build()
	)
}