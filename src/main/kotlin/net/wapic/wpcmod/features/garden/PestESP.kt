package net.wapic.wpcmod.features.garden

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.components.GlowableESPConfig
import net.wapic.wpcmod.features.entity.EspFeature
import net.wapic.wpcmod.util.EntityUtils.headTexture
import net.wapic.wpcmod.util.HeadTextures
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils

object PestESP : EspFeature() {

	val config get() = WpcMod.config.garden.esp.pest

	fun init() = Unit

	override fun compute(entity: Entity): GlowableESPConfig? = config.takeIf {
		entity is ArmorStand && entity.headTexture in HeadTextures.allPests
	}

	override fun isEnabled(): Boolean {
		return Utils.getLocation() == Island.GARDEN && (config.glow || config.tracer || config.box)
	}
}