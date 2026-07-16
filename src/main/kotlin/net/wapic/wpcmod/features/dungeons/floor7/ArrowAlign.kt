package net.wapic.wpcmod.features.dungeons.floor7

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ItemFrame
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.DungeonUtils.F7Phase
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.render.WorldRenderContext

object ArrowAlign {
	private val config get() = WpcMod.config.dungeon.floor7.arrowAlign

    private val frameGridCorner = BlockPos(-2, 120, 75)
    private val recentClickTimestamps = mutableMapOf<Int, Long>()
    private val clicksRemaining = mutableMapOf<Int, Int>()
    private var currentFrameRotations: List<Int>? = null
    private var targetSolution: List<Int>? = null

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
		WorldRenderEvent.EVENT.register(::onRenderWorld)
		UseEntityCallback.EVENT.register(::onEntityInteract)
    }

	fun onTick(client: Minecraft) {
		if (DungeonUtils.getF7Phase() != F7Phase.GOLDOR || !config.enabled) return

		clicksRemaining.clear()
		if ((client.player?.position()?.distanceTo(Vec3(0.0, 120.0, 77.0)) ?: return) > 200) {
			currentFrameRotations = null
			targetSolution = null
			return
		}
		currentFrameRotations = getFrames()

		possibleSolutions.forEach { arr ->
			for (i in arr.indices) {
				if ((arr[i] == -1 || currentFrameRotations?.get(i) == -1) && arr[i] != currentFrameRotations?.get(i)) return@forEach
			}

			targetSolution = arr

			for (i in arr.indices) {
				clicksRemaining[i] = calculateClicksNeeded(currentFrameRotations?.get(i) ?: return@forEach, arr[i]).takeIf { it != 0 } ?: continue
			}
		}
	}

	//TODO: fix interaction results
	fun onEntityInteract(
		player: Player,
		level: Level,
		hand: InteractionHand,
		entity: Entity,
		hitResult: EntityHitResult
	): InteractionResult {
		if (DungeonUtils.getF7Phase() != F7Phase.GOLDOR || !config.enabled) return InteractionResult.PASS
		if (entity !is ItemFrame || entity.item.item != Items.ARROW) return InteractionResult.PASS

		val frameIndex = ((entity.blockY - frameGridCorner.y) + (entity.blockZ - frameGridCorner.z) * 5)
		if (entity.blockX != frameGridCorner.x || currentFrameRotations?.get(frameIndex) == -1 || frameIndex !in 0..24) return InteractionResult.PASS

		if (!clicksRemaining.containsKey(frameIndex) && MC.player?.isShiftKeyDown == config.invertSneak && config.blockWrongClick) {
			return InteractionResult.FAIL
		}

		recentClickTimestamps[frameIndex] = System.currentTimeMillis()
		currentFrameRotations =
			currentFrameRotations?.toMutableList()?.apply { this[frameIndex] = (this[frameIndex] + 1) % 8 }

		if (calculateClicksNeeded(
				currentFrameRotations?.get(frameIndex) ?: return InteractionResult.PASS,
				targetSolution?.get(frameIndex) ?: return InteractionResult.PASS
			) == 0
		) clicksRemaining.remove(frameIndex)
		return InteractionResult.PASS
	}

    fun onRenderWorld(worldRenderContext: WorldRenderContext) {
        if (clicksRemaining.isEmpty() || DungeonUtils.getF7Phase() != F7Phase.GOLDOR || !config.enabled) return
		worldRenderContext.profiler.push("ArrowAlign")

		/*
				clicksRemaining.forEach { (index, clickNeeded) ->
					val colorCode = when {
						clickNeeded == 0 -> return@forEach
						clickNeeded < 3 -> 'a'
						clickNeeded < 5 -> '6'
						else -> 'c'
					}
					worldRenderContext.drawText(
						Component.nullToEmpty("§$colorCode$clickNeeded").visualOrderText,
						getFramePositionFromIndex(index).center.add(Vec3(-0.3, 0.0, 0.1)),
						1f,

						false
					)
				}
		 */
		worldRenderContext.profiler.pop()
    }

    private fun getFrames(): List<Int> {
		val itemFrames = MC.level?.entitiesForRendering()?.mapNotNull {
			if (it is ItemFrame && it.item.item.asItem() == Items.ARROW) it else null
        }?.takeIf { it.isNotEmpty() } ?: return List(25) { -1 }

        return (0..24).map { index ->
            if (recentClickTimestamps[index]?.let { System.currentTimeMillis() - it < 1000 } == true && currentFrameRotations != null)
                currentFrameRotations?.get(index) ?: -1
            else
                itemFrames.find { it.blockPosition() == getFramePositionFromIndex(index) }?.rotation ?: -1
        }
    }

    private fun getFramePositionFromIndex(index: Int): BlockPos =
        frameGridCorner.offset(0, index % 5, index / 5)

    private fun calculateClicksNeeded(currentRotation: Int, targetRotation: Int): Int =
        (8 - currentRotation + targetRotation) % 8

    private val possibleSolutions = listOf(
        listOf(7, 7, -1, -1, -1, 1, -1, -1, -1, -1, 1, 3, 3, 3, 3, -1, -1, -1, -1, 1, -1, -1, -1, 7, 1),
        listOf(-1, -1, 7, 7, 5, -1, 7, 1, -1, 5, -1, -1, -1, -1, -1, -1, 7, 5, -1, 1, -1, -1, 7, 7, 1),
        listOf(7, 7, -1, -1, -1, 1, -1, -1, -1, -1, 1, 3, -1, 7, 5, -1, -1, -1, -1, 5, -1, -1, -1, 3, 3),
        listOf(5, 3, 3, 3, -1, 5, -1, -1, -1, -1, 7, 7, -1, -1, -1, 1, -1, -1, -1, -1, 1, 3, 3, 3, -1),
        listOf(5, 3, 3, 3, 3, 5, -1, -1, -1, 1, 7, 7, -1, -1, 1, -1, -1, -1, -1, 1, -1, 7, 7, 7, 1),
        listOf(7, 7, 7, 7, -1, 1, -1, -1, -1, -1, 1, 3, 3, 3, 3, -1, -1, -1, -1, 1, -1, 7, 7, 7, 1),
        listOf(-1, -1, -1, -1, -1, 1, -1, 1, -1, 1, 1, -1, 1, -1, 1, 1, -1, 1, -1, 1, -1, -1, -1, -1, -1),
        listOf(-1, -1, -1, -1, -1, 1, 3, 3, 3, 3, -1, -1, -1, -1, 1, 7, 7, 7, 7, 1, -1, -1, -1, -1, -1),
        listOf(-1, -1, -1, -1, -1, -1, 1, -1, 1, -1, 7, 1, 7, 1, 3, 1, -1, 1, -1, 1, -1, -1, -1, -1, -1)
    )
}