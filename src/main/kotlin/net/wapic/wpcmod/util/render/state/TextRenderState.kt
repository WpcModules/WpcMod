package net.wapic.wpcmod.util.render.state

import io.github.notenoughupdates.moulconfig.ChromaColour
import org.joml.Vector3f

@JvmRecord
data class TextRenderState(
	val text: String,
	val pos: Vector3f,
	val color: ChromaColour,
	val scale: Float,
	val shadow: Boolean,
	val background: Boolean,
) : RenderState