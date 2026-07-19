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
import net.wapic.wpcmod.util.lerpedEyePos
import net.wapic.wpcmod.util.render.state.EspRenderState
import net.wapic.wpcmod.util.renderPos

object GalateaESP : EspFeature() {

	private val config get() = WpcMod.config.galatea.esp

	fun init() = Unit

	fun isInvisibug(entity: ArmorStand): Boolean {
		return entity.deltaMovement.x == 0.0 && entity.deltaMovement.y != 0.0 && entity.deltaMovement.z == 0.0 && entity.isMarker
	}

	override fun compute(entity: Entity): EspRenderState? {
		return when (entity) {
			is Shulker -> EspRenderState(config.shulker, entity.bbWidth, entity.bbHeight, entity.renderPos)
			is Axolotl -> EspRenderState(config.axolotl, entity.bbWidth, entity.bbHeight, entity.renderPos)
			is Frog -> EspRenderState(config.frog, entity.bbWidth, entity.bbHeight, entity.renderPos)
			is Panda -> EspRenderState(config.panda, entity.bbWidth, entity.bbHeight, entity.renderPos)
			is Pufferfish -> EspRenderState(config.pufferfish, entity.bbWidth, entity.bbHeight, entity.renderPos)
			is Turtle -> EspRenderState(config.shellwise, entity.bbWidth, entity.bbHeight, entity.renderPos)
			is ArmorStand -> if(isInvisibug(entity)) EspRenderState(config.invisibug, .5f, .5f, entity.lerpedEyePos.add(.0, .75, .0)) else null
			else -> null
		}
	}

	override fun isEnabled(): Boolean {
		return Utils.getLocation() == Island.GALATEA
	}
}