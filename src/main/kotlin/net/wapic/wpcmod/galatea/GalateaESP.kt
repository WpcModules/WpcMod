package net.wapic.wpcmod.galatea

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
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.PacketEvents
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.RenderUtils

object GalateaESP {

    val forestNodes: MutableSet<Box> = mutableSetOf()
    val config = WpcMod.config.instance.galateaConfig.espSettings
    data class ESPSettings(var box: Boolean, var tracer: Boolean, var color: ChromaColour)

    fun init() {
        WorldRenderEvents.END.register(::renderWorld)

        //Forest Nodes
        PacketEvents.PARTICLE.register(::onParticle)
        AttackBlockCallback.EVENT.register(::onAttackBlock)
        UseBlockCallback.EVENT.register(::onUseBlock)
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register { client, world ->
            forestNodes.clear()
        }
    }

    fun getSettings(entity: Entity): ESPSettings {
        when(entity) {
            is ShulkerEntity -> return ESPSettings(config.shulkerSettings.box, config.shulkerSettings.tracer, config.shulkerSettings.color)
            is AxolotlEntity -> return ESPSettings(config.axolotlSettings.box, config.axolotlSettings.tracer, config.axolotlSettings.color)
            is FrogEntity -> return ESPSettings(config.frogSettings.box, config.frogSettings.tracer, config.frogSettings.color)
            is PandaEntity -> return ESPSettings(config.pandaSettings.box, config.pandaSettings.tracer, config.pandaSettings.color)
            is PufferfishEntity -> return ESPSettings(config.pufferfishSettings.box, config.pufferfishSettings.tracer, config.pufferfishSettings.color)
            is ArmorStandEntity -> return ESPSettings(
                config.invisibugSettings.box && entity.velocity != Vec3d.ZERO && entity.y > 92 && entity.boundingBox.averageSideLength == 0.0,
                config.invisibugSettings.tracer && entity.velocity != Vec3d.ZERO && entity.y > 92 && entity.boundingBox.averageSideLength == 0.0,
                config.invisibugSettings.color
            )
        }
        return ESPSettings(false, false, ChromaColour(1f,1f,1f,0,0xff))
    }

    fun stringCount(entity: DisplayEntity.ItemDisplayEntity): Boolean {
        return !entity.itemStack.isEmpty && entity.itemStack.item.equals(Items.STRING)
    }

    fun onAttackBlock(player: PlayerEntity, world: World, hand: Hand, pos: BlockPos, dir: Direction): ActionResult {
        if(Utils.getLocation() != Island.GALATEA) return ActionResult.PASS
        forestNodes.remove(Box.of(pos.toCenterPos(), 1.0, 1.0, 1.0))
        return ActionResult.PASS
    }

    fun onUseBlock(player: PlayerEntity, world: World, hand: Hand, hitResult: BlockHitResult): ActionResult  {
        if(Utils.getLocation() != Island.GALATEA) return ActionResult.PASS
        forestNodes.remove(Box.of(hitResult.blockPos.toCenterPos(), 1.0, 1.0, 1.0))
        return ActionResult.PASS
    }

    fun onParticle(packet: ParticleS2CPacket, world: ClientWorld) {
        if(Utils.getLocation() != Island.GALATEA || !config.forestNodeSettings.box || !config.forestNodeSettings.tracer) return

        if(ParticleTypes.HAPPY_VILLAGER.type.equals(packet.parameters.type)) {
            val pos: BlockPos = BlockPos.ofFloored(packet.x, packet.y - 1, packet.z)
            val box = Box.of(pos.toCenterPos(), 1.0, 1.0, 1.0)

            if(forestNodes.contains(box)) return

            val entities: List<DisplayEntity.ItemDisplayEntity> = world.getEntitiesByClass(
                DisplayEntity.ItemDisplayEntity::class.java, box) {
                    entities -> true
            }

            if(entities.count(::stringCount) == 3) forestNodes.add(box)
        }
    }

    fun renderWorld(worldRenderContext: WorldRenderContext) {
        if(Utils.getLocation() != Island.GALATEA) return

        worldRenderContext.world().entities.forEach { entity ->
            val settings = getSettings(entity)
            if(settings.box) RenderUtils.drawBoundingBox(worldRenderContext, if(entity.boundingBox.averageSideLength == 0.0) entity.boundingBox.expand(0.5) else entity.boundingBox, color = settings.color.getEffectiveColour())
            if(settings.tracer) RenderUtils.drawTracer(worldRenderContext, entity.x, entity.y + entity.height / 2, entity.z, color = settings.color.getEffectiveColour())
        }

        forestNodes.forEach { node ->
            if(config.forestNodeSettings.box) RenderUtils.drawBox(worldRenderContext, node.minX, node.maxY, node.minZ, node.maxX, node.maxY, node.maxZ, color = config.forestNodeSettings.color.getEffectiveColour())
            if(config.forestNodeSettings.tracer) RenderUtils.drawTracer(worldRenderContext, node.center.x, node.maxY, node.center.z, color = config.forestNodeSettings.color.getEffectiveColour())
        }
    }
}