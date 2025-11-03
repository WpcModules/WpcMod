package net.wapic.wpcmod.features.galatea

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.decoration.DisplayEntity
import net.minecraft.entity.mob.ShulkerEntity
import net.minecraft.entity.passive.*
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.ParticleEvents
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.features.entity.MobGlowCache
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.WorldRenderContext
import java.util.concurrent.CopyOnWriteArraySet

object GalateaESP : MobGlowCache() {

	private var forestNodes = CopyOnWriteArraySet<Box>()
	private val config get() = WpcMod.config.galatea.esp

	fun init() {
		WorldRenderEvent.EVENT.register(::renderWorld)

		// Forest Nodes
		ParticleEvents.SPAWN.register(::onParticle)
		AttackBlockCallback.EVENT.register { _, _, _, pos, _ -> onBlockInteract(pos) }
		UseBlockCallback.EVENT.register { _, _, _, hitResult -> onBlockInteract(hitResult.blockPos) }
		WorldChangeEvent.AFTER.register { _ -> forestNodes.clear() }
	}

	private fun stringCount(entity: DisplayEntity.ItemDisplayEntity): Boolean {
		return !entity.itemStack.isEmpty && entity.itemStack.item.equals(Items.STRING)
	}

	private fun onBlockInteract(pos: BlockPos): ActionResult {
		if (isEnabled()) {
			forestNodes.removeIf { it == Box.of(pos.toCenterPos(), 1.0, 1.0, 1.0) }
		}

		return ActionResult.PASS
	}

	private fun onParticle(packet: ParticleS2CPacket, world: ClientWorld) {
		if (!isEnabled()) return
		if (!config.forestNode.tracer && !config.forestNode.box) return

		if (ParticleTypes.HAPPY_VILLAGER.type.equals(packet.parameters.type)) {

			val pos: BlockPos = BlockPos.ofFloored(packet.x, packet.y - 1, packet.z)
			val box = Box.of(pos.toCenterPos(), 1.0, 1.0, 1.0)

			if (forestNodes.contains(box)) return

			val entities: List<DisplayEntity.ItemDisplayEntity> =
				world.getEntitiesByClass(DisplayEntity.ItemDisplayEntity::class.java, box) { true }
			if (entities.count(::stringCount) == 3) forestNodes.add(box)
		}
	}

	private fun isInvisibug(entity: Entity): Boolean {
		return entity.velocity != Vec3d.ZERO && entity.boundingBox.averageSideLength == 0.0 && entity.y > 92 && entity is ArmorStandEntity
	}


	private fun renderWorld(worldRenderContext: WorldRenderContext) {
		if (!isEnabled()) return

		worldRenderContext.profiler.push("galatea-esp")
		for (entity in worldRenderContext.world.entities) {
			var boundingBox = entity.boundingBox

			val settings = when {
				entity is ShulkerEntity -> config.shulker
 				entity is AxolotlEntity -> config.axolotl
				entity is FrogEntity -> config.frog
				entity is PandaEntity -> config.panda
				entity is PufferfishEntity -> config.pufferfish
				entity is TurtleEntity -> config.shellwise
				isInvisibug(entity) -> {
					boundingBox = entity.boundingBox.expand(0.5).offset(0.0, 0.75, 0.0)
					config.invisibug
				}
				else -> continue
			}

			if (settings.box)
				worldRenderContext.drawBoundingBox(entity.boundingBox, settings.color)

			if (settings.tracer)
				worldRenderContext.drawTracer(boundingBox.center, settings.color)
		}

		worldRenderContext.profiler.swap("forest-nodes")
		if(config.forestNode.box || config.forestNode.tracer) {
			for (node in forestNodes) {
				if (config.forestNode.box) {
					worldRenderContext.drawBoundingBox(
						node.withMinY(node.maxY),
						config.forestNode.color
					)
				}
				if (config.forestNode.tracer) {
					worldRenderContext.drawTracer(
						node.withMinY(node.maxY).center,
						config.forestNode.color
					)
				}
			}
		}
		worldRenderContext.profiler.pop()
	}

	override fun compute(entity: Entity): ChromaColour? {
		return when {
			entity is ShulkerEntity && config.shulker.glow -> config.shulker.color
			entity is AxolotlEntity && config.axolotl.glow -> config.axolotl.color
			entity is FrogEntity && config.frog.glow -> config.frog.color
			entity is PandaEntity && config.panda.glow -> config.panda.color
			entity is PufferfishEntity && config.pufferfish.glow -> config.pufferfish.color
			entity is TurtleEntity && config.shellwise.glow -> config.shellwise.color
			else -> null
		}
	}

	override fun isEnabled(): Boolean {
		return Utils.getLocation() == Island.GALATEA
	}
}