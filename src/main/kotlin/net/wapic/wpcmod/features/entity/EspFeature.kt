package net.wapic.wpcmod.features.entity

import net.minecraft.world.entity.Entity
import net.wapic.wpcmod.config.components.GlowableESPConfig

abstract class EspFeature {

	constructor() {
		EspCache.add(this)
	}

	abstract fun compute(entity: Entity): GlowableESPConfig?

	abstract fun isEnabled(): Boolean
}