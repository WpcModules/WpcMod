package net.wapic.wpcmod.features.galatea

import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.animal.axolotl.Axolotl
import net.minecraft.world.entity.animal.fish.Pufferfish
import net.minecraft.world.entity.animal.frog.Frog
import net.minecraft.world.entity.animal.panda.Panda
import net.minecraft.world.entity.animal.turtle.Turtle
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.monster.Shulker
import net.minecraft.world.item.Items
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.components.GlowableESPConfig
import net.wapic.wpcmod.events.ParticleEvents
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.features.entity.EspFeature
import net.wapic.wpcmod.util.EntityUtils.getRenderPos
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.WorldRenderContext
import java.util.concurrent.CopyOnWriteArraySet

object GalateaESP : EspFeature() {

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

	private fun renderInvisibugESP(worldRenderContext: WorldRenderContext) {
		val deltaTicks = worldRenderContext.tickCounter.getGameTimeDeltaPartialTick(true)
		val invisibugs = MC.entitiesOf<ArmorStand>().filter(::isInvisibug)

		for (entity in invisibugs) {
			val pos = entity.getRenderPos(deltaTicks)
			with(config.invisibug) {
				if (box) worldRenderContext.drawBoundingBox(pos, 1f, 1f, color)
				if (tracer) worldRenderContext.drawTracer(pos, color, tracerWidth)
			}
		}
	}

	private fun renderForestNodeESP(worldRenderContext: WorldRenderContext) {
		for (node in forestNodes) {
			with(config.forestNode) {
				if (box) worldRenderContext.drawBoundingBox(node.setMinY(node.maxY), color)
				if (tracer) worldRenderContext.drawTracer(node.setMinY(node.maxY).center, color)
			}
		}
	}

	private fun renderWorld(worldRenderContext: WorldRenderContext) {
		if (!isEnabled()) return

		worldRenderContext.profiler.push("galatea-esp")
		if (config.invisibug.box || config.invisibug.tracer) {
			renderInvisibugESP(worldRenderContext)
		}

		worldRenderContext.profiler.popPush("forest-nodes")
		if(config.forestNode.box || config.forestNode.tracer) {
			renderForestNodeESP(worldRenderContext)
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