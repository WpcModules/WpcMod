package net.wapic.wpcmod.features.entity

import net.minecraft.world.entity.Entity
import net.wapic.wpcmod.util.render.state.EspRenderState

abstract class EspFeature {

	constructor() {
		EspCache.add(this)
	}

	abstract fun compute(entity: Entity): EspRenderState?

	abstract fun isEnabled(): Boolean
}