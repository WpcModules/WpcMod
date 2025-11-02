package net.wapic.wpcmod.util.render

import io.github.notenoughupdates.moulconfig.ChromaColour

val ChromaColour.Companion.BLACK get() = ChromaColour.fromStaticRGB(0, 0, 0, 255)
val ChromaColour.Companion.WHITE get() = ChromaColour.fromStaticRGB(255, 255, 255, 255)

fun ChromaColour.darker(): ChromaColour {
	return ChromaColour(hue, saturation, (brightness - 0.25f).coerceIn(0f..1f), timeForFullRotationInMillis, alpha)
}

fun ChromaColour.brighter(): ChromaColour {
	return ChromaColour(hue, saturation, (brightness + 0.25f).coerceIn(0f..1f), timeForFullRotationInMillis, alpha)
}