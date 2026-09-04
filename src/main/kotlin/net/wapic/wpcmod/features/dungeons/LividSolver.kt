package net.wapic.wpcmod.features.dungeons

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.block.StainedGlassBlock
import net.minecraft.world.level.block.state.BlockState
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.BlockEvents
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.features.entity.EspFeature
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.util.copyWithColor
import net.wapic.wpcmod.util.dungeons.DungeonUtils
import net.wapic.wpcmod.util.dungeons.DungeonUtils.DungeonFloor
import net.wapic.wpcmod.util.dungeons.DungeonUtils.currentFloor
import net.wapic.wpcmod.util.render.state.EntityState
import net.wapic.wpcmod.util.render.toChromaColour

object LividSolver : EspFeature() {

	private val config get() = WpcMod.config.dungeon.lividSolver
	private val isInLividBossRoom
		get() = currentFloor.equalsOneOf(
			DungeonFloor.FLOOR_5,
			DungeonFloor.MASTER_MODE_FLOOR_5
		) && DungeonUtils.bossSpawned

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

	fun init() {
		BlockEvents.CHANGE.register(::onBlockChange)
		WorldChangeEvent.BEFORE.register(::reset)
	}

	private fun onBlockChange(pos: BlockPos, oldState: BlockState?, newState: BlockState) {
		if (!isEnabled()) return
		if (pos != centerBlockPosition) return

		correctColor = (newState.block as? StainedGlassBlock)?.color
			?: return ChatUtils.sendMessage("Unable to find correct livid")
		WpcMod.LOGGER.debug("Correct livid color set to: {}", correctColor)
	}

	private fun reset(world: ClientLevel) {
		correctColor = DyeColor.RED
	}

	override fun compute(entity: Entity): EntityState? {
		if (entity.plainTextName != lividTypes[correctColor]) return null
		val usedConfig =
			if (config.useLividColor) config.copyWithColor(correctColor.textColor.toChromaColour()) else config
		return EntityState(usedConfig)
	}

	override fun isEnabled(): Boolean = isInLividBossRoom && (config.glow || config.tracer || config.box)
}