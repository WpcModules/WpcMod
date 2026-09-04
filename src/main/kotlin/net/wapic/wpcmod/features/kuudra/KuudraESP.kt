package net.wapic.wpcmod.features.kuudra

import net.minecraft.world.entity.Entity
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.entity.EspFeature
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.KuudraUtils
import net.wapic.wpcmod.util.KuudraUtils.Phase
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.state.EntityState

object KuudraESP : EspFeature() {

	private val config get() = WpcMod.config.kuudra.esp

	fun init() = Unit

	override fun compute(entity: Entity): EntityState? {
		if (entity != KuudraUtils.kuudraEntity) return null
		if (config.kuudra.killPhaseOnly && KuudraUtils.phase != Phase.KILL) return null
		return EntityState(config.kuudra)
	}

	override fun isEnabled(): Boolean {
		return Utils.getLocation() == Island.KUUDRA
	}
}