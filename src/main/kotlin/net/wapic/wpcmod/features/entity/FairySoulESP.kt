package net.wapic.wpcmod.features.entity

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.headTexture
import net.wapic.wpcmod.util.HeadTextures
import net.wapic.wpcmod.util.render.state.EspRenderState
import net.wapic.wpcmod.util.lerpedEyePos

object FairySoulESP : EspFeature() {
	private val config get() = WpcMod.config.general.esp.fairySoul

	fun init() = Unit

	override fun compute(entity: Entity): EspRenderState? {
		if(entity is ArmorStand && entity.headTexture == HeadTextures.FAIRY_SOUL) {
			return EspRenderState(config, 0.8f, 0.8f, entity.lerpedEyePos)
		}
		return null
	}

	override fun isEnabled(): Boolean = config.tracer || config.box || config.glow
}