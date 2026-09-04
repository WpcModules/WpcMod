package net.wapic.wpcmod.util.render.state

import net.wapic.wpcmod.config.components.EspConfig

@JvmRecord
data class EntityState(
	val config: EspConfig,
	val width: Float? = null,
	val height: Float? = null,
	val yOffset: Float? = null
)