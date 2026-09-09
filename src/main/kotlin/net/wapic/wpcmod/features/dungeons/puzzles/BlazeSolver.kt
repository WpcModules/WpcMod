package net.wapic.wpcmod.features.dungeons.puzzles

import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.monster.Blaze
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.floor7.TickTimers.onWorldChange
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.Room
import net.wapic.wpcmod.util.ChatUtils.removeFormatting
import net.wapic.wpcmod.util.render.WpcModExtractionContext
import net.wapic.wpcmod.util.render.darker

object BlazeSolver {
	private val config get() = WpcMod.config.dungeon.puzzles.blazeSolver
	private var blazeType: BlazeType = BlazeType.NONE

	fun init() {
		WorldRenderEvent.EVENT.register(::onRenderWorld)
		DungeonEvents.ROOM_ENTERED.register(::onRoomEntered)
		WorldChangeEvent.AFTER.register(::onWorldChange)
	}

	fun onRenderWorld(context: WpcModExtractionContext, profiler: ProfilerFiller) {
		if (blazeType == BlazeType.NONE || !config.enabled) return

		var blazeNameTags = context.level.entitiesForRendering().filter { it is ArmorStand && it.name.string.contains("Blaze") }
		if (blazeNameTags.isEmpty()) return

		blazeNameTags = blazeNameTags.sortedBy { it.name.string.removeFormatting().replace(Regex("\\D"),"").takeLast(4).toInt() }

		if (blazeType == BlazeType.LOWER) blazeNameTags = blazeNameTags.reversed()
		for (i in 0..(blazeNameTags.size-1).coerceAtMost(config.blazesToShow.toInt() - 1)) {
			val color = when (i) {
				0 -> config.blazeColor0
				1 -> config.blazeColor1
				else -> config.blazeColor2
			}
			val blaze = getAssociatedBlaze(blazeNameTags[i]) ?: continue
			if(config.filled) context.filledAABB(blaze.boundingBox, color.darker(), color) else context.aabb(blaze.boundingBox, color)

			if(i > 0) {
				val prevBlaze = getAssociatedBlaze(blazeNameTags[i - 1]) ?: continue
				if(i < config.linesToShow) context.line(blaze.boundingBox.center, prevBlaze.boundingBox.center, color, config.lineWidth)
			} else {
				if(config.linesToShow > 0) context.tracer(blaze.boundingBox.center, color, config.lineWidth)
			}
		}
	}

	fun onRoomEntered(oldRoom: Room, newRoom: Room) {
		blazeType = when (newRoom.data.name) {
			"Lower Blaze" -> BlazeType.LOWER
			"Higher Blaze" -> BlazeType.HIGHER
			else -> BlazeType.NONE
		}
	}

	fun onWorldChange() {
		blazeType = BlazeType.NONE
	}

	fun getAssociatedBlaze(entity: Entity): Blaze? {
		return entity.level().getEntitiesOfClass(Blaze::class.java, entity.boundingBox.expandTowards(0.0, -1.0, 0.0)).firstOrNull()
	}

	enum class BlazeType {
		LOWER,
		HIGHER,
		NONE
	}
}