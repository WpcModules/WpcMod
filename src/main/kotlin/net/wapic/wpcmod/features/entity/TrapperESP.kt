package net.wapic.wpcmod.features.entity

import net.minecraft.world.entity.Entity
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.components.GlowableESPConfig
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils

object TrapperESP : EspFeature() {

	private val config get() = WpcMod.config.general.esp.trapperAnimals
	private val names = listOf("Trackable", "Untrackable", "Undetected", "Endangered", "Elusive")

	fun init() = Unit

	override fun compute(entity: Entity): GlowableESPConfig? = config.takeIf {
		names.any { entity.name.string.contains(it) }
	}

	override fun isEnabled(): Boolean =
		Utils.getLocation() == Island.BARN && (config.tracer || config.box || config.glow)
}
