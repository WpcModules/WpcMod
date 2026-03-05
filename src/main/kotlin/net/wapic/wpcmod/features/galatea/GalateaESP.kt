package net.wapic.wpcmod.features.galatea

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.animal.Panda
import net.minecraft.world.entity.animal.Pufferfish
import net.minecraft.world.entity.animal.Turtle
import net.minecraft.world.entity.animal.axolotl.Axolotl
import net.minecraft.world.entity.animal.frog.Frog
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.monster.Shulker
import net.minecraft.world.item.Items
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
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

	private var forestNodes = CopyOnWriteArraySet<AABB>()
	private val config get() = WpcMod.config.galatea.esp

	fun init() {
		WorldRenderEvent.EVENT.register(::renderWorld)

		// Forest Nodes
		ParticleEvents.SPAWN.register(::onParticle)
		AttackBlockCallback.EVENT.register { _, _, _, pos, _ -> onBlockInteract(pos) }
		UseBlockCallback.EVENT.register { _, _, _, hitResult -> onBlockInteract(hitResult.blockPos) }
		WorldChangeEvent.AFTER.register { _ -> forestNodes.clear() }
	}

	private fun stringCount(entity: Display.ItemDisplay): Boolean {
		return !entity.itemStack.isEmpty && entity.itemStack.item.equals(Items.STRING)
	}

	private fun onBlockInteract(pos: BlockPos): InteractionResult {
		if (isEnabled()) {
			forestNodes.removeIf { it == AABB.ofSize(pos.center, 1.0, 1.0, 1.0) }
		}

		return InteractionResult.PASS
	}

	private fun onParticle(packet: ClientboundLevelParticlesPacket, world: ClientLevel) {
		if (!isEnabled()) return
		if (!config.forestNode.tracer && !config.forestNode.box) return

		if (ParticleTypes.HAPPY_VILLAGER.type.equals(packet.particle.type)) {

			val pos: BlockPos = BlockPos.containing(packet.x, packet.y - 1, packet.z)
			val box = AABB.ofSize(pos.center, 1.0, 1.0, 1.0)

			if (forestNodes.contains(box)) return

			val entities: List<Display.ItemDisplay> =
				world.getEntitiesOfClass(Display.ItemDisplay::class.java, box) { true }
			if (entities.count(::stringCount) == 3) forestNodes.add(box)
		}
	}

	private fun isInvisibug(entity: Entity): Boolean {
		return entity.deltaMovement != Vec3.ZERO && entity.boundingBox.size == 0.0 && entity.y > 92 && entity is ArmorStand
	}


	private fun renderWorld(worldRenderContext: WorldRenderContext) {
		if (!isEnabled()) return

		worldRenderContext.profiler.push("galatea-esp")
		for (entity in worldRenderContext.level.entitiesForRendering()) {
			var boundingBox = entity.boundingBox

			val settings = when {
				entity is Shulker -> config.shulker
 				entity is Axolotl -> config.axolotl
				entity is Frog -> config.frog
				entity is Panda -> config.panda
				entity is Pufferfish -> config.pufferfish
				entity is Turtle -> config.shellwise
				isInvisibug(entity) -> {
					boundingBox = entity.boundingBox.inflate(0.5).move(0.0, 0.75, 0.0)
					config.invisibug
				}
				else -> continue
			}

			if (settings.box)
				worldRenderContext.drawBoundingBox(boundingBox, settings.color)

			if (settings.tracer)
				worldRenderContext.drawTracer(boundingBox.center, settings.color)
		}

		worldRenderContext.profiler.popPush("forest-nodes")
		if(config.forestNode.box || config.forestNode.tracer) {
			for (node in forestNodes) {
				if (config.forestNode.box) {
					worldRenderContext.drawBoundingBox(
						node.setMinY(node.maxY),
						config.forestNode.color
					)
				}
				if (config.forestNode.tracer) {
					worldRenderContext.drawTracer(
						node.setMinY(node.maxY).center,
						config.forestNode.color
					)
				}
			}
		}
		worldRenderContext.profiler.pop()
	}

	override fun compute(entity: Entity): ChromaColour? {
		return when {
			entity is Shulker && config.shulker.glow -> config.shulker.color
			entity is Axolotl && config.axolotl.glow -> config.axolotl.color
			entity is Frog && config.frog.glow -> config.frog.color
			entity is Panda && config.panda.glow -> config.panda.color
			entity is Pufferfish && config.pufferfish.glow -> config.pufferfish.color
			entity is Turtle && config.shellwise.glow -> config.shellwise.color
			else -> null
		}
	}

	override fun isEnabled(): Boolean {
		return Utils.getLocation() == Island.GALATEA
	}
}