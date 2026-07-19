package net.wapic.wpcmod.util.render.state

import net.minecraft.world.phys.Vec3
import net.wapic.wpcmod.config.components.EspConfig

data class EspRenderState(
	val config: EspConfig,
	val width: Float,
	val height: Float,
	val pos: Vec3,
)