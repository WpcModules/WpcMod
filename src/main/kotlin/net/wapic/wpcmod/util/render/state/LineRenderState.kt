package net.wapic.wpcmod.util.render.state

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.wapic.wpcmod.util.render.WHITE
import org.joml.Vector3fc

@JvmRecord
data class LineRenderState(
	val firstPos: Vector3fc,
	val secondPos: Vector3fc,
	val color: ChromaColour = ChromaColour.WHITE,
	val lineWidth: Float = 1f
) : RenderState