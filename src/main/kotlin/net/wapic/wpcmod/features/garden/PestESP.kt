package net.wapic.wpcmod.features.garden

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.entity.EspFeature
import net.wapic.wpcmod.util.HeadTextures
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.headTexture
import net.wapic.wpcmod.util.render.state.EntityState

object PestESP : EspFeature() {

	private val config get() = WpcMod.config.garden.esp.pest

	fun init() = Unit

	override fun compute(entity: Entity): EntityState? {
		if (entity is ArmorStand && entity.headTexture in HeadTextures.allPests) {
			return EntityState(config, .8f, .8f, 1.35f)
		}

		return null
	}

	override fun isEnabled(): Boolean {
		return Utils.getLocation() == Island.GARDEN && (config.glow || config.tracer || config.box)
	}
}