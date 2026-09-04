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
import net.wapic.wpcmod.util.render.state.EntityState

object GalateaESP : EspFeature() {

	private val config get() = WpcMod.config.galatea.esp

	fun init() = Unit

	fun isInvisibug(entity: ArmorStand): Boolean {
		return entity.deltaMovement.x == 0.0 && entity.deltaMovement.y != 0.0 && entity.deltaMovement.z == 0.0 && entity.isMarker
	}

	override fun compute(entity: Entity): EntityState? {
		val config = when (entity) {
			is Shulker -> config.shulker
			is Axolotl -> config.axolotl
			is Frog -> config.frog
			is Panda -> config.panda
			is Pufferfish -> config.pufferfish
			is Turtle -> config.shellwise
			is ArmorStand -> return if (isInvisibug(entity)) EntityState(config.invisibug, .8f, .8f, .35f) else null
			else -> return null
		}
		return EntityState(config)
	}

	override fun isEnabled(): Boolean {
		return Utils.getLocation() == Island.GALATEA
	}
}