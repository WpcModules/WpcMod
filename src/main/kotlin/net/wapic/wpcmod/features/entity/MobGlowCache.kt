package net.wapic.wpcmod.features.entity

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.world.entity.Entity

abstract class MobGlowCache {

	constructor() {
		MobGlow.add(this)
	}

	abstract fun compute(entity: Entity): ChromaColour?

	abstract fun isEnabled(): Boolean
}