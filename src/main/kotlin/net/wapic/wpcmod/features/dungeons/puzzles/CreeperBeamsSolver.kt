package net.wapic.wpcmod.features.dungeons.puzzles

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.core.BlockPos
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.Room
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.RunOnStartup
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.util.render.WpcModExtractionContext

object CreeperBeamsSolver {

	private val config get() = WpcMod.config.dungeon.puzzles.creeperBeams
	private val targetLines = mutableListOf<Pair<Vec3, Vec3>>()
	private val colors = listOf(
		ChromaColour.fromStaticRGB(255, 0, 0, 255),
		ChromaColour.fromStaticRGB(0, 255, 0, 255),
		ChromaColour.fromStaticRGB(0, 0, 255, 255),
		ChromaColour.fromStaticRGB(255, 0, 255, 255),
		ChromaColour.fromStaticRGB(255, 255, 0, 255),
		ChromaColour.fromStaticRGB(0, 255, 255, 255),
		ChromaColour.fromStaticRGB(255, 100, 0, 255),
		ChromaColour.fromStaticRGB(125, 0, 255, 255),
	)

	@RunOnStartup
	fun init() {
		DungeonEvents.ROOM_ENTERED.register(::onRoomEntered)
		WorldRenderEvent.EVENT.register(::onWorldRender)
		WorldChangeEvent.AFTER.register { targetLines.clear() }
	}

	private fun onRoomEntered(oldRoom: Room, newRoom: Room) {
		targetLines.clear()
		if (newRoom.data.name != "Creeper Beams" || !config.enabled) return

		val level = MC.level ?: return
		val uniqueRoom = newRoom.uniqueRoom ?: return ChatUtils.sendMessage("Unable to find unique room")

		val centerBox = AABB(uniqueRoom.getCenterBlockPos(77)).move(0.0, -0.5, 0.0)
		val roomBoundingBox = centerBox.inflate(13.0, 7.5, 13.0)
		val lanterns = mutableListOf<Vec3>()

		for (pos in BlockPos.betweenClosed(roomBoundingBox)) {
			if (level.getBlockState(pos).block.equalsOneOf(Blocks.SEA_LANTERN, Blocks.PRISMARINE)) {
				lanterns.add(Vec3.atCenterOf(pos))
			}
		}

		for (firstVec in lanterns) {
			if (targetLines.any { it.second == firstVec }) continue

			for (secondVec in lanterns) {
				if (firstVec == secondVec || targetLines.any { it.second == secondVec || it.first == secondVec }) continue

				if (centerBox.clip(firstVec, secondVec).isPresent) {
					targetLines.add(firstVec to secondVec)
					break
				}
			}
		}

		targetLines.sortBy { it.first.distanceToSqr(it.second) }
	}

	private fun onWorldRender(context: WpcModExtractionContext, profiler: ProfilerFiller) {
		if (targetLines.isEmpty()) return

		val mutableColors = colors.toMutableList()
		for ((start, end) in targetLines) {
			val color = mutableColors.removeFirst()
			context.box(start.subtract(0.5), 1f, 1f, color, true)
			context.box(end.subtract(0.5), 1f, 1f, color, true)
			context.line(start, end, color, config.lineWidth)
		}
	}
}