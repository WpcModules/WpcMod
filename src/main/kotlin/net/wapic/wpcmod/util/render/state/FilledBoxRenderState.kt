package net.wapic.wpcmod.util.render.state

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.wapic.wpcmod.util.render.WHITE

data class FilledBoxRenderState(
	val x: Float,
	val y: Float,
	val z: Float,
	val x2: Float,
	val y2: Float,
	val z2: Float,
	val color: ChromaColour = ChromaColour.WHITE,
) : RenderState