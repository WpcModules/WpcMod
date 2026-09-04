package net.wapic.wpcmod.util.render.state

import io.github.notenoughupdates.moulconfig.ChromaColour
import org.joml.Quaternionf
import org.joml.Vector3f

data class TextRenderState(
	val text: String,
	val pos: Vector3f,
	val color: ChromaColour,
	val scale: Float,
	val shadow: Boolean,
	val background: Boolean,
	val cameraPos: Vector3f,
	val cameraOrientation: Quaternionf
) : RenderState