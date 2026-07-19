package net.wapic.wpcmod.features.entity

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.entity.animal.equine.Horse
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.TrapperAPI
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.state.EspRenderState
import net.wapic.wpcmod.util.renderPos
import net.wapic.wpcmod.util.skyBlockMaxHealth

object TrapperESP : EspFeature() {

	private val config get() = WpcMod.config.general.esp.trapperAnimals
	private val names = listOf("Trackable", "Untrackable", "Undetected", "Endangered", "Elusive")

	fun init() = Unit

	fun isTrapperAnimal(entity: Entity): Boolean {
		if (entity is Horse) return TrapperAPI.currentType?.maxHealth == entity.skyBlockMaxHealth / 2
		return TrapperAPI.currentType?.maxHealth == (entity as? Animal).skyBlockMaxHealth
	}

	override fun compute(entity: Entity): EspRenderState? {
		if (isTrapperAnimal(entity)) {
			return EspRenderState(config, entity.bbWidth, entity.bbHeight, entity.renderPos)
		}
		return null
	}

	override fun isEnabled(): Boolean =
		Utils.getLocation() == Island.BARN && (config.tracer || config.box || config.glow)
}
