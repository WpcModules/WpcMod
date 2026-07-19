package net.wapic.wpcmod.util.render

import com.mojang.blaze3d.PrimitiveTopology
import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.pipeline.ColorTargetState
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import net.minecraft.client.renderer.RenderPipelines
import net.wapic.wpcmod.WpcMod
import java.util.*

object WpcModRenderPipelines {

	val LINES: RenderPipeline = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withLocation(WpcMod.Identifier("pipeline/wpcmod_lines"))
			.withDepthStencilState(Optional.empty())
			.build()
	)

	val QUADS: RenderPipeline = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
			.withLocation(WpcMod.Identifier("pipeline/wpcmod_quads"))
			.withVertexShader("core/position_color")
			.withFragmentShader("core/position_color")
			.withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
			.withPrimitiveTopology(PrimitiveTopology.QUADS)
			.withColorTargetState(ColorTargetState(BlendFunction.TRANSLUCENT))
			.withDepthStencilState(Optional.empty())
			.withCull(false)
			.build()
	)

	val GUI_CUSTOM: RenderPipeline = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
			.withLocation(WpcMod.Identifier("pipeline/wpcmod_gui_rounded"))
			.withVertexShader(WpcMod.Identifier("core/rendertype_rr"))
			.withFragmentShader(WpcMod.Identifier("core/rendertype_rr"))
			.withColorTargetState(ColorTargetState(BlendFunction.TRANSLUCENT))
			.withVertexBinding(0, CustomVertexFormat.ROUNDED_RECTANGLE)
			.withPrimitiveTopology(PrimitiveTopology.QUADS)
			.withCull(true)
			.build()
	)
}