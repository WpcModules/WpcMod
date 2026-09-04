package net.wapic.wpcmod.util.render.state

import net.wapic.wpcmod.config.components.EspConfig
import org.joml.Vector3f

data class EspRenderState(
	val config: EspConfig,
	val pos: Vector3f,
	val width: Float,
	val height: Float,
	val camera: Vector3f
) : RenderState