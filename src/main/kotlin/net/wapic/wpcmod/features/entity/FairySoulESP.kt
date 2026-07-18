package net.wapic.wpcmod.features.entity

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.components.GlowableESPConfig
import net.wapic.wpcmod.util.headTexture
import net.wapic.wpcmod.util.HeadTextures

object FairySoulESP : EspFeature() {
	private val config get() = WpcMod.config.general.esp.fairySoul

	fun init() = Unit

	override fun compute(entity: Entity): GlowableESPConfig? = config.takeIf {
		entity is ArmorStand && entity.headTexture == HeadTextures.FAIRY_SOUL
	}

	override fun isEnabled(): Boolean = config.tracer || config.box || config.glow
}