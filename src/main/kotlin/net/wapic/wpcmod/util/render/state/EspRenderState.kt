package net.wapic.wpcmod.util.render.state

import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import net.wapic.wpcmod.config.components.EspConfig
import net.wapic.wpcmod.util.lerpedEyePos
import net.wapic.wpcmod.util.lerpedRenderPos
import net.wapic.wpcmod.util.up

data class EspRenderState(
	val config: EspConfig,
	val width: Float,
	val height: Float,
	val pos: Vec3,
) {
	companion object {
		fun fromEntity(entity: Entity, config: EspConfig): EspRenderState {
			return EspRenderState(config, entity.bbWidth, entity.bbHeight, entity.lerpedRenderPos)
		}

		fun fromArmorStand(entity: Entity, config: EspConfig, yOffset: Double = 0.0): EspRenderState {
			return EspRenderState(config, 0.8f, 0.8f, entity.lerpedEyePos.up(yOffset))
		}
	}
}