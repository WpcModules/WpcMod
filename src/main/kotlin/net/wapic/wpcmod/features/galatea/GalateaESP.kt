package net.wapic.wpcmod.features.galatea

import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.animal.axolotl.Axolotl
import net.minecraft.world.entity.animal.fish.Pufferfish
import net.minecraft.world.entity.animal.frog.Frog
import net.minecraft.world.entity.animal.panda.Panda
import net.minecraft.world.entity.animal.turtle.Turtle
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.monster.Shulker
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.components.GlowableESPConfig
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.features.entity.EspFeature
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.WorldRenderContext

object GalateaESP : EspFeature() {

	private val config get() = WpcMod.config.galatea.esp

	fun init() {
		WorldRenderEvent.EVENT.register(::renderWorld)
	}

	private fun renderWorld(worldRenderContext: WorldRenderContext) {
		if (!isEnabled()) return
		if (!config.invisibug.box && !config.invisibug.tracer) return

		worldRenderContext.profiler.push("invisibug-esp")
		val invisibugs = MC.entitiesOf<ArmorStand>().filter {
			it.deltaMovement.x == 0.0 && it.deltaMovement.y != 0.0 && it.deltaMovement.z == 0.0 && it.isMarker
		}

		for (entity in invisibugs) {
			val pos = entity.eyePosition.relative(Direction.UP, 0.5)
			if (config.invisibug.box) worldRenderContext.drawBoundingBox(pos, 1f, 1f, config.invisibug.color)
			if (config.invisibug.tracer) worldRenderContext.drawTracer(
				pos,
				config.invisibug.color,
				config.invisibug.tracerWidth
			)
		}
		worldRenderContext.profiler.pop()
	}

	override fun compute(entity: Entity): GlowableESPConfig? {
		return when (entity) {
			is Shulker -> config.shulker
			is Axolotl -> config.axolotl
			is Frog -> config.frog
			is Panda -> config.panda
			is Pufferfish -> config.pufferfish
			is Turtle -> config.shellwise
			else -> null
		}
	}

	override fun isEnabled(): Boolean {
		return Utils.getLocation() == Island.GALATEA
	}
}