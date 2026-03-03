package net.wapic.wpcmod.features.dungeons

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.RemotePlayer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.BlockEvents
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.features.entity.MobGlowCache
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.DungeonUtils.DungeonFloor
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.util.render.WorldRenderContext

object LividSolver : MobGlowCache() {

	private val config get() = WpcMod.config.dungeon.lividSolver
	private val isFloor5
		get() = DungeonUtils.currentFloor.equalsOneOf(
			DungeonFloor.FLOOR_5,
			DungeonFloor.MASTER_MODE_FLOOR_5
		)
	private val lividTypes = mapOf(
		Blocks.WHITE_STAINED_GLASS to "Vendetta Livid",
		Blocks.MAGENTA_STAINED_GLASS to "Crossed Livid",
		Blocks.YELLOW_STAINED_GLASS to "Arcade Livid",
		Blocks.LIME_STAINED_GLASS to "Smile Livid",
		Blocks.GRAY_STAINED_GLASS to "Doctor Livid",
		Blocks.PURPLE_STAINED_GLASS to "Purple Livid",
		Blocks.BLUE_STAINED_GLASS to "Scream Livid",
		Blocks.GREEN_STAINED_GLASS to "Frog Livid",
		Blocks.RED_STAINED_GLASS to "Hockey Livid"
	)

	private val centerBlockPosition = BlockPos(5, 108, 42)
	private var correctLivid: String? = lividTypes[Blocks.RED_STAINED_GLASS]
	private var livid: Entity? = null

	fun init() {
		BlockEvents.CHANGE.register(::onBlockChange)
		WorldRenderEvent.EVENT.register(::onRenderWorld)
		WorldChangeEvent.BEFORE.register(::reset)
	}

	fun shouldRun() = config.enabled && isFloor5 && DungeonUtils.bossSpawned

	fun onBlockChange(pos: BlockPos, oldState: BlockState, newState: BlockState) {
		if (!shouldRun()) return
		if (pos == centerBlockPosition && newState.block in lividTypes.keys) {
			correctLivid = lividTypes[newState.block]
				?: return ChatUtils.sendMessage("Unable to find correct livid! block found: ${newState.block}")
		}
	}

	fun onRenderWorld(worldRenderContext: WorldRenderContext) {
		if (!shouldRun()) return
		livid = worldRenderContext.level.entitiesForRendering()
			?.find { it is RemotePlayer && it.plainTextName == correctLivid }

		livid?.let {
			val deltaTicks = worldRenderContext.tickCounter.getGameTimeDeltaPartialTick(true)
			val position = it.getPosition(deltaTicks).relative(Direction.UP, 1.0)

			if (config.box) worldRenderContext.drawBoundingBox(position, 0.5f, 1.85f, config.color)
			if (config.lineWidth > 0) worldRenderContext.drawTracer(position, config.color, config.lineWidth.toDouble())
		}
	}

	fun reset(world: ClientLevel) {
		correctLivid = lividTypes[Blocks.RED_STAINED_GLASS]
		livid = null
	}

	override fun compute(entity: Entity): ChromaColour? {
		return if (entity == livid) config.color else null
	}

	override fun isEnabled(): Boolean {
		return shouldRun() && config.glow
	}
}