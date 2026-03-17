package net.wapic.wpcmod.features.entity

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.components.GlowableESPConfig
import net.wapic.wpcmod.util.EntityUtils.headTexture
import net.wapic.wpcmod.util.HeadTextures
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils

object RatESP : EspFeature() {

	private val config get() = WpcMod.config.general.esp.rat

	fun init() = Unit

	override fun compute(entity: Entity): GlowableESPConfig? = config.takeIf {
		entity is ArmorStand && entity.headTexture == HeadTextures.RAT
	}

	override fun isEnabled(): Boolean =
		Utils.getLocation() == Island.HUB && (config.tracer || config.box || config.glow)
}