package net.wapic.wpcmod.features.entity

import net.minecraft.world.entity.Display
import net.minecraft.world.entity.Entity
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.HeadTextures
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.headTexture
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.state.EspRenderState
import net.wapic.wpcmod.util.lerpedEyePos

object RatESP : EspFeature() {

	private val config get() = WpcMod.config.general.esp.rat

	fun init() = Unit

	override fun compute(entity: Entity): EspRenderState? {
		if (entity is Display.ItemDisplay && entity.itemStack.headTexture == HeadTextures.RAT) {
			return EspRenderState(config, 0.8f, 0.8f, entity.lerpedEyePos.add(.0, .3, .0))
		}
		return null
	}

	override fun isEnabled(): Boolean =
		Utils.getLocation() == Island.HUB && (config.tracer || config.box || config.glow)
}