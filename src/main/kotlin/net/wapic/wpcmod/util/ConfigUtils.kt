package net.wapic.wpcmod.util

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.wapic.wpcmod.config.components.GlowableESPConfig

object ConfigUtils {
	fun GlowableESPConfig.copyWithColor(newColor: ChromaColour): GlowableESPConfig {
		return object : GlowableESPConfig() {
			init {
				color = newColor
				box = this@copyWithColor.box
				tracer = this@copyWithColor.tracer
				tracerWidth = this@copyWithColor.tracerWidth
				glow = this@copyWithColor.glow
			}
		}
	}
}