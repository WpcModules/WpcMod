package net.wapic.wpcmod.features.dungeons.floor7

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.util.profiling.ProfilerFiller
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
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.dungeons.DungeonUtils
import net.wapic.wpcmod.util.dungeons.DungeonUtils.F7Phase
import net.wapic.wpcmod.util.render.WpcModExtractionContext

object ArrowAlign {

	private val config get() = WpcMod.config.dungeon.floor7.arrowAlign

	private val frameGridCorner = BlockPos(-2, 120, 75)
	private val recentClickTimestamps = mutableMapOf<Int, Long>()
	private val clicksRemaining = mutableMapOf<Int, Int>()
	private var currentFrameRotations: List<Int>? = null
	private var targetSolution: List<Int>? = null

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
		UseEntityCallback.EVENT.register(::onEntityInteract)
		WorldRenderEvent.EVENT.register(::onRenderWorld)
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
				clicksRemaining[i] =
					calculateClicksNeeded(currentFrameRotations?.get(i) ?: return@forEach, arr[i]).takeIf { it != 0 }
						?: continue
			}
		}
	}

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

		if (!clicksRemaining.containsKey(frameIndex) && player.isShiftKeyDown == config.invertSneak && config.blockWrongClick) {
			return InteractionResult.FAIL
		}

		recentClickTimestamps[frameIndex] = System.currentTimeMillis()
		currentFrameRotations = currentFrameRotations?.toMutableList()?.apply { this[frameIndex] = (this[frameIndex] + 1) % 8 }
		val currentRotation = currentFrameRotations?.get(frameIndex) ?: return InteractionResult.PASS
		val targetRotation = targetSolution?.get(frameIndex) ?: return InteractionResult.PASS

		if (calculateClicksNeeded(currentRotation, targetRotation) == 0) clicksRemaining.remove(frameIndex)
		return InteractionResult.PASS
	}

	fun onRenderWorld(context: WpcModExtractionContext, profiler: ProfilerFiller) {
		if (clicksRemaining.isEmpty() || DungeonUtils.getF7Phase() != F7Phase.GOLDOR || !config.enabled) return
		profiler.push("ArrowAlign")
		clicksRemaining.forEach { (index, clickNeeded) ->
			val color = when {
				clickNeeded == 0 -> return@forEach
				clickNeeded < 3 -> ChromaColour.fromStaticRGB(0, 255, 0, 255)
				clickNeeded < 5 -> ChromaColour.fromStaticRGB(255, 150, 0, 255)
				else -> ChromaColour.fromStaticRGB(255, 0, 0, 255)
			}
			val pos = getFramePositionFromIndex(index)
			context.text("$clickNeeded", Vec3(pos.x - 0.3, pos.y.toDouble(), pos.z + 0.1), color, 1.5f, true, false)
		}
		profiler.pop()
	}

	private fun getFrames(): List<Int> {
		val itemFrames = MC.level?.entitiesForRendering()?.mapNotNull {
			if (it is ItemFrame && it.item.item == Items.ARROW) it else null
		}?.takeIf { it.isNotEmpty() } ?: return List(25) { -1 }

		return (0..24).map { index ->
			if (recentClickTimestamps[index]?.let { System.currentTimeMillis() - it < 1000 } == true && currentFrameRotations != null)
				currentFrameRotations?.get(index) ?: -1
			else
				itemFrames.find { it.blockPosition() == getFramePositionFromIndex(index) }?.rotation ?: -1
		}
	}

	private fun getFramePositionFromIndex(index: Int): BlockPos = frameGridCorner.offset(0, index % 5, index / 5)

	private fun calculateClicksNeeded(currentRotation: Int, targetRotation: Int): Int = (8 - currentRotation + targetRotation) % 8

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