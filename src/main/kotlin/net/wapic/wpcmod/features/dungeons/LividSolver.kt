package net.wapic.wpcmod.features.dungeons

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.RemotePlayer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.block.StainedGlassBlock
import net.minecraft.world.level.block.state.BlockState
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.BlockEvents
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.features.entity.MobGlowCache
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.DungeonUtils.DungeonFloor
import net.wapic.wpcmod.util.DungeonUtils.currentFloor
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.util.render.WorldRenderContext
import net.wapic.wpcmod.util.render.toChromaColour

object LividSolver : MobGlowCache() {

	private val config get() = WpcMod.config.dungeon.lividSolver
	private val isFloor5 get() = currentFloor.equalsOneOf(DungeonFloor.FLOOR_5, DungeonFloor.MASTER_MODE_FLOOR_5)
	private val shouldRun get() = (config.box || (config.tracer && config.tracerWidth > 0)) && isFloor5 && DungeonUtils.bossSpawned

	private val renderColor
		get() = correctColor.textColor.toChromaColour().takeIf { config.useLividColor } ?: config.color

	private val centerBlockPosition = BlockPos(5, 108, 42)
	private val lividTypes = mapOf(
		DyeColor.WHITE to "Vendetta Livid",
		DyeColor.MAGENTA to "Crossed Livid",
		DyeColor.YELLOW to "Arcade Livid",
		DyeColor.LIME to "Smile Livid",
		DyeColor.GRAY to "Doctor Livid",
		DyeColor.PURPLE to "Purple Livid",
		DyeColor.BLUE to "Scream Livid",
		DyeColor.GREEN to "Frog Livid",
		DyeColor.RED to "Hockey Livid"
	)

	private var correctColor: DyeColor = DyeColor.RED
	private var livid: Entity? = null

	fun init() {
		BlockEvents.CHANGE.register(::onBlockChange)
		WorldRenderEvent.EVENT.register(::onRenderWorld)
		WorldChangeEvent.BEFORE.register(::reset)
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
	}

	private fun onBlockChange(pos: BlockPos, oldState: BlockState, newState: BlockState) {
		if (!shouldRun) return
		if (pos != centerBlockPosition) return

		correctColor = (newState.block as? StainedGlassBlock)?.color
			?: return ChatUtils.sendMessage("Unable to find correct livid")
	}

	private fun onTick(client: Minecraft) {
		if (!shouldRun) return

		val target = lividTypes[correctColor] ?: return
		livid = livid?.takeIf { it.isAlive && it.plainTextName == target } ?: MC.entitiesOf<RemotePlayer>()
			.find { it.plainTextName == target }
	}

	private fun onRenderWorld(worldRenderContext: WorldRenderContext) {
		if (!shouldRun) return

		livid?.let {
			val deltaTicks = worldRenderContext.tickCounter.getGameTimeDeltaPartialTick(true)
			val position = it.getPosition(deltaTicks).relative(Direction.UP, 1.0)

			if (config.box) worldRenderContext.drawBoundingBox(position, 0.5f, 1.85f, renderColor)
			if (config.tracer && config.tracerWidth > 0) worldRenderContext.drawTracer(
				position,
				renderColor,
				config.tracerWidth
			)
		}
	}

	private fun reset(world: ClientLevel) {
		correctColor = DyeColor.RED
		livid = null
	}

	override fun compute(entity: Entity): ChromaColour? = renderColor.takeIf { entity == livid }
	override fun isEnabled(): Boolean = shouldRun && config.glow
}