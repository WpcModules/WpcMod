package net.wapic.wpcmod.features.dungeons.floor7

import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.ServerTickEvent
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.dungeons.DungeonUtils
import net.wapic.wpcmod.util.skyblockId

object AutoDebuff {

	private val config get() = WpcMod.config.dungeon.floor7.debuff
	private val isEnabled get() = config.enabled && DungeonUtils.currentFloor == DungeonUtils.DungeonFloor.MASTER_MODE_FLOOR_7
	private val validItems = listOf("LAST_BREATH", "STARRED_LAST_BREATH")
	private var tick = -1

	fun init() {
		ServerTickEvent.EVENT.register(::onServerTick)
		UseItemCallback.EVENT.register(::onUseItem)
	}

	fun onServerTick() {
		if (tick < 0) return
		tick++

		if (tick >= config.releaseTick) {
			MC.options.keyUse.isDown = false
			reset()
		}
	}

	fun onUseItem(player: Player, level: Level, hand: InteractionHand): InteractionResult {
		if (isEnabled && player.mainHandItem.skyblockId in validItems) {
			tick = 0
		}
		return InteractionResult.PASS
	}

	fun reset() {
		tick = -1
	}
}