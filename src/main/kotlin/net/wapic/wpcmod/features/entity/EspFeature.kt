package net.wapic.wpcmod.features.entity

import net.minecraft.world.entity.Entity
import net.wapic.wpcmod.util.render.state.EntityState

abstract class EspFeature {

	constructor() {
		EspCache.add(this)
	}

	abstract fun compute(entity: Entity): EntityState?

	abstract fun isEnabled(): Boolean
}