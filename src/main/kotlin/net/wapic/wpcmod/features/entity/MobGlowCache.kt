package net.wapic.wpcmod.features.entity

import net.minecraft.entity.Entity

abstract class MobGlowCache {

	constructor() {
		MobGlow.add(this)
	}

	abstract fun compute(entity: Entity): Int

	abstract fun isEnabled(): Boolean
}