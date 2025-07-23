package net.wapic.wpcmod.features.end

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.boss.dragon.EnderDragonEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.RenderUtils

class EndESP {

    private var endNodes: MutableSet<Box> = mutableSetOf()
    private val config get() = WpcMod.config.endConfig.espSettings
    data class ESPSettings(var box: Boolean, var tracer: Boolean, var color: ChromaColour)

    init {
        WorldRenderEvents.END.register(::renderWorld)
        ClientTickEvents.END_WORLD_TICK.register(::worldTick)
    }

    private fun getSettings(entity: Entity): ESPSettings {
        when(entity) {
            is EnderDragonEntity -> return ESPSettings(config.dragonSettings.box, config.dragonSettings.tracer, config.dragonSettings.color)
        }
        return ESPSettings(false, false, ChromaColour(1f,1f,1f,0,0xff))
    }

    private var lock = false
    private fun worldTick(world: ClientWorld) {
        if(Utils.getLocation() != Island.END) return

        if(lock) return

        val player = MinecraftClient.getInstance().player
        val radius = config.endNodeSettings.radius.toInt()

        val newEndNodes: MutableSet<Box> = mutableSetOf()
        lock = true
        player?.let {
            for (x in -radius..radius) {
                for (y in -radius..radius) {
                    for (z in -radius..radius) {
                        val pos = BlockPos.ofFloored(player.x + x, player.y + y, player.z + z)
                        if(world.getBlockState(pos).block == Blocks.PURPLE_TERRACOTTA) {
                            val box = Box.of(pos.toCenterPos(), 1.0, 1.0, 1.0)
                            newEndNodes.add(box)
                        }
                    }
                }
            }
        }
        lock = false
        endNodes = newEndNodes
    }

    private fun renderWorld(worldRenderContext: WorldRenderContext) {
        if(Utils.getLocation() != Island.END) return

        worldRenderContext.world().entities.forEach { entity ->
            val settings = getSettings(entity)
            if(settings.box) RenderUtils.drawBoundingBox(worldRenderContext, if(entity.boundingBox.averageSideLength == 0.0) entity.boundingBox.expand(0.5) else entity.boundingBox, color = settings.color.getEffectiveColour())
            if(settings.tracer) RenderUtils.drawTracer(worldRenderContext, entity.x, entity.y + entity.height / 2, entity.z, color = settings.color.getEffectiveColour())
        }

        endNodes.forEach { node ->
            if(config.endNodeSettings.box) RenderUtils.drawBox(worldRenderContext, node.minX, node.minY, node.minZ, node.maxX, node.maxY, node.maxZ, color = config.endNodeSettings.color.getEffectiveColour())
            if(config.endNodeSettings.tracer) RenderUtils.drawTracer(worldRenderContext, node.center.x, node.maxY, node.center.z, color = config.endNodeSettings.color.getEffectiveColour())
        }
    }
}