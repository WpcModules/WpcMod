package net.wapic.wpcmod.util.render

import net.minecraft.client.renderer.rendertype.LayeringTransform
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType

object WpcModRenderTypes {

	val LINES = RenderType.create(
		"wpcmod_lines",
		RenderSetup.builder(WpcModRenderPipelines.LINES)
			.bufferSize(RenderType.TRANSIENT_BUFFER_SIZE)
			.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
			.createRenderSetup()
	)

	val FILLED_BOX: RenderType = RenderType.create(
		"wpcmod_filled_box",
		RenderSetup.builder(WpcModRenderPipelines.FILLED_BOX)
			.bufferSize(RenderType.TRANSIENT_BUFFER_SIZE)
			.setLayeringTransform(LayeringTransform.NO_LAYERING)
			.createRenderSetup()
	)
}