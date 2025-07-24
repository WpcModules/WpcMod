package net.wapic.wpcmod.features.galatea

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.decoration.DisplayEntity
import net.minecraft.entity.mob.ShulkerEntity
import net.minecraft.entity.passive.AxolotlEntity
import net.minecraft.entity.passive.FrogEntity
import net.minecraft.entity.passive.PandaEntity
import net.minecraft.entity.passive.PufferfishEntity
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.ParticleEvents
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.RenderUtils

class GalateaESP {

	private var forestNodes: MutableSet<Box> = mutableSetOf()
	private val config get() = WpcMod.config.galateaConfig.espSettings

	data class ESPSettings(var box: Boolean, var tracer: Boolean, var color: ChromaColour)

	init {
		WorldRenderEvents.END.register(::renderWorld)

		// Forest Nodes
		ParticleEvents.SPAWN.register(::onParticle)
		AttackBlockCallback.EVENT.register { _, _, _, pos, _ -> onBlockInteract(pos) }
		UseBlockCallback.EVENT.register { _, _, _, hitResult -> onBlockInteract(hitResult.blockPos) }
		ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register { _, _ -> forestNodes.clear() }
	}

	private fun getSettings(entity: Entity): ESPSettings {
		return when (entity) {
			is ShulkerEntity -> ESPSettings(
				config.shulkerSettings.box,
				config.shulkerSettings.tracer,
				config.shulkerSettings.color
			)

			is AxolotlEntity -> ESPSettings(
				config.axolotlSettings.box,
				config.axolotlSettings.tracer,
				config.axolotlSettings.color
			)

			is FrogEntity -> ESPSettings(config.frogSettings.box, config.frogSettings.tracer, config.frogSettings.color)
			is PandaEntity -> ESPSettings(
				config.pandaSettings.box,
				config.pandaSettings.tracer,
				config.pandaSettings.color
			)

			is PufferfishEntity -> ESPSettings(
				config.pufferfishSettings.box,
				config.pufferfishSettings.tracer,
				config.pufferfishSettings.color
			)

			is ArmorStandEntity -> ESPSettings(
				config.invisibugSettings.box && entity.velocity != Vec3d.ZERO && entity.y > 92 && entity.boundingBox.averageSideLength == 0.0,
				config.invisibugSettings.tracer && entity.velocity != Vec3d.ZERO && entity.y > 92 && entity.boundingBox.averageSideLength == 0.0,
				config.invisibugSettings.color
			)

			else -> ESPSettings(box = false, tracer = false, color = ChromaColour(1f, 1f, 1f, 0, 0xff))
		}
	}

	private fun stringCount(entity: DisplayEntity.ItemDisplayEntity): Boolean {
		return !entity.itemStack.isEmpty && entity.itemStack.item.equals(Items.STRING)
	}

	private fun onBlockInteract(pos: BlockPos): ActionResult {
		if (Utils.getLocation() != Island.GALATEA) return ActionResult.PASS
		forestNodes.removeIf { it == Box.of(pos.toCenterPos(), 1.0, 1.0, 1.0) }
		return ActionResult.PASS
	}

	private fun onParticle(packet: ParticleS2CPacket, world: ClientWorld) {
		if (Utils.getLocation() != Island.GALATEA) return
		if (!config.forestNodeSettings.tracer && !config.forestNodeSettings.box) return

		val newForestNodes = mutableSetOf<Box>()
		if (ParticleTypes.HAPPY_VILLAGER.type.equals(packet.parameters.type)) {

			val pos: BlockPos = BlockPos.ofFloored(packet.x, packet.y - 1, packet.z)
			val box = Box.of(pos.toCenterPos(), 1.0, 1.0, 1.0)

			val entities: List<DisplayEntity.ItemDisplayEntity> =
				world.getEntitiesByClass(DisplayEntity.ItemDisplayEntity::class.java, box) { true }
			if (entities.count(::stringCount) == 3) newForestNodes.add(box)
		}
		forestNodes = newForestNodes
	}

	private fun renderWorld(worldRenderContext: WorldRenderContext) {
		if (Utils.getLocation() != Island.GALATEA) return

		worldRenderContext.world().entities.forEach { entity ->
			val settings = getSettings(entity)
			if (settings.box) RenderUtils.drawBoundingBox(
				worldRenderContext,
				if (entity.boundingBox.averageSideLength == 0.0) entity.boundingBox.expand(0.5) else entity.boundingBox,
				color = settings.color.getEffectiveColour()
			)
			if (settings.tracer) RenderUtils.drawTracer(
				worldRenderContext,
				entity.x,
				entity.y + entity.height / 2,
				entity.z,
				color = settings.color.getEffectiveColour()
			)
		}

		forestNodes.forEach { node ->
			if (config.forestNodeSettings.box) RenderUtils.drawBox(
				worldRenderContext,
				node.minX,
				node.maxY,
				node.minZ,
				node.maxX,
				node.maxY,
				node.maxZ,
				color = config.forestNodeSettings.color.getEffectiveColour()
			)
			if (config.forestNodeSettings.tracer) RenderUtils.drawTracer(
				worldRenderContext,
				node.center.x,
				node.maxY,
				node.center.z,
				color = config.forestNodeSettings.color.getEffectiveColour()
			)
		}
	}
}