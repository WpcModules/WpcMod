package net.wapic.wpcmod.features.galatea

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.animal.axolotl.Axolotl
import net.minecraft.world.entity.animal.fish.Pufferfish
import net.minecraft.world.entity.animal.frog.Frog
import net.minecraft.world.entity.animal.panda.Panda
import net.minecraft.world.entity.animal.turtle.Turtle
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.monster.Shulker
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.entity.EspFeature
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.state.EspRenderState

object GalateaESP : EspFeature() {

	private val config get() = WpcMod.config.galatea.esp

	fun init() = Unit

	fun isInvisibug(entity: ArmorStand): Boolean {
		return entity.deltaMovement.x == 0.0 && entity.deltaMovement.y != 0.0 && entity.deltaMovement.z == 0.0 && entity.isMarker
	}

	override fun compute(entity: Entity): EspRenderState? {
		return when (entity) {
			is Shulker -> EspRenderState.fromEntity(entity, config.shulker)
			is Axolotl -> EspRenderState.fromEntity(entity, config.axolotl)
			is Frog -> EspRenderState.fromEntity(entity, config.frog)
			is Panda -> EspRenderState.fromEntity(entity, config.panda)
			is Pufferfish -> EspRenderState.fromEntity(entity, config.pufferfish)
			is Turtle -> EspRenderState.fromEntity(entity, config.shellwise)
			is ArmorStand ->
				if(isInvisibug(entity)) EspRenderState.fromArmorStand(entity, config.invisibug, .75) else null
			else -> null
		}
	}

	override fun isEnabled(): Boolean {
		return Utils.getLocation() == Island.GALATEA
	}
}